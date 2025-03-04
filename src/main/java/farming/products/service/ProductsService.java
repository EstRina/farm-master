package farming.products.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import farming.accounting.dto.UserType;
import farming.accounting.entity.UserAccount;
import farming.customer.entity.Customer;
import farming.customer.repo.CustomerRepository;
import farming.farmer.entity.Farmer;
import farming.farmer.repo.FarmerRepository;
import farming.products.dto.ProductDto;
import farming.products.dto.RemoveProductDataDto;
import farming.products.dto.SaleRecordsDto;
import farming.products.dto.SurpriseBagDto;
import farming.products.entity.Product;
import farming.products.entity.RemoveProductData;
import farming.products.entity.SaleRecords;
import farming.products.entity.SurpriseBag;
import farming.products.repo.ProductsRepository;
import farming.products.repo.RemoveProductDataRepository;
import farming.products.repo.SaleRecordsRepository;
import farming.products.repo.SurpriseBagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsService implements IProductsService{

	private final ProductsRepository productRepo;
    private final FarmerRepository farmerRepo;
    private final SaleRecordsRepository saleRecordsRepo;
    private final CustomerRepository customerRepo;
    private final RemoveProductDataRepository removeProductDataRepo;
    private final SurpriseBagRepository surpriseBagRepo;
	
    private void checkFarmerRole(UserAccount user) {
        if (user == null || !user.getUserType().equals(UserType.FARMER)) {
            log.warn("User {} is not a farmer, access denied", user != null ? user.getLogin() : "null");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only farmers can perform this operation");
        }
    }
    
    private void checkCustomerRole(UserAccount user) {
        if (user == null || !user.getUserType().equals(UserType.CUSTOMER)) {
            log.warn("User {} is not a customer, access denied", user != null ? user.getLogin() : "null");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customers can perform this operation");
        }
    }
    
	@Override
	@Transactional
	public ProductDto addProduct(ProductDto productDto, UserAccount user) {
	    log.info("Adding product: {} by user: {}", productDto != null ? productDto.getProductName() : "null", 
	             user != null ? user.getLogin() : "null");
	    
	    if (user == null) {
	        log.error("User is not authenticated");
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
	    }
	    
	    if (productDto == null || productDto.getProductName() == null) {
	        log.error("ProductDto or productName is null");
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product data must not be null");
	    }
	    
	    checkFarmerRole(user);

	    Farmer farmer = farmerRepo.findByUserAccountLogin(user.getLogin())
	            .orElseThrow(() -> {
	                log.error("Farmer profile not found for user: {}", user.getLogin());
	                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer profile not found");
	            });

	    Product product;
	    try {
	        product = Product.of(productDto);
	        product.setFarmers(Collections.singletonList(farmer));
	        productRepo.save(product);
	    } catch (Exception e) {
	        log.error("Failed to save product: {}", e.getMessage(), e);
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save product", e);
	    }

	    log.debug("Product added with ID: {} by farmer: {}", product.getId(), farmer.getFarmerId());
	    return product.toDto();  // Возвращаем ProductDto с заполненным farmer
	}

	@Override
	@Transactional
	public boolean updateProduct(ProductDto productDto, UserAccount user) {
        log.info("Updating product with ID: {} by user: {}", productDto.getProductId(), user.getLogin());
        checkFarmerRole(user);
        Product product = productRepo.findById(productDto.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productDto.getProductId());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
                });
        Farmer farmer = farmerRepo.findByUserAccountLogin(user.getLogin())
                .orElseThrow(() -> {
                    log.error("Farmer profile not found for user: {}", user.getLogin());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer profile not found");
                });
        if (!product.getFarmers().stream().anyMatch(f -> f.getFarmerId().equals(farmer.getFarmerId()))) {
            log.warn("Farmer {} does not own product ID {}, access denied", farmer.getFarmerId(), productDto.getProductId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update products you own");
        }
        if (productDto.getProductName() != null) product.setProductName(productDto.getProductName());
        if (productDto.getQuantity() >= 0) product.setQuantity(productDto.getQuantity());
        if (productDto.getPrice() != null) product.setPrice(productDto.getPrice());
        if (productDto.getImgUrl() != null) product.setImgUrl(productDto.getImgUrl());
        productRepo.save(product);
        log.debug("Product updated: {}", product);
        return true;
    }

	@Override
    @Transactional
    public RemoveProductDataDto removeProduct(Long productId, Long farmerId, UserAccount user) {
		log.info("Removing product ID {} for farmer ID {} by user: {}", productId, farmerId, user != null ? 
				user.getLogin() : "null");
	    if (user == null) {
	        log.error("User is not authenticated");
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
	    }
	    checkFarmerRole(user);
	    
	    log.debug("Fetching product with ID: {}", productId);
	    Product product = productRepo.findById(productId)
	            .orElseThrow(() -> {
	                log.error("Product not found with ID: {}", productId);
	                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
	            });
	    
	    log.debug("Fetching farmer with ID: {}", farmerId);
	    Farmer farmer = farmerRepo.findById(farmerId)
	            .orElseThrow(() -> {
	                log.error("Farmer not found with ID: {}", farmerId);
	                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
	            });
	    
	    log.debug("Checking if user {} matches farmer ID {}", user.getLogin(), farmerId);
	    if (!farmer.getUserAccount().getLogin().equals(user.getLogin())) {
	        log.warn("User {} is not the owner farmer ID {}, access denied", user.getLogin(), farmerId);
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove products you own");
	    }
	    
	    log.debug("Checking if farmer ID {} owns product ID {}", farmerId, productId);
	    if (product.getFarmers() == null || !product.getFarmers().stream().anyMatch(f -> f.getFarmerId().equals(farmerId))) {
	        log.warn("Farmer ID {} does not own product ID {}", farmerId, productId);
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer doesn't own this product");
	    }
	    
	    log.debug("Removing farmer ID {} from product ID {}", farmerId, productId);
	    product.getFarmers().removeIf(f -> f.getFarmerId().equals(farmerId));
	    productRepo.saveAndFlush(product);

	    log.debug("Fetching sales for product ID {}", productId);
	    List<SaleRecords> sales = saleRecordsRepo.findByProduct(product);
	    
	    log.debug("Creating and saving RemoveProductData for product ID {}", productId);
	    RemoveProductData removeData = RemoveProductData.builder()
	            .product(product)
	            .saleRecords(sales)
	            .build();
	    removeProductDataRepo.saveAndFlush(removeData);

	    if (product.getFarmers().isEmpty()) {
	        log.info("Marking product ID {} as deleted as no farmers remain", productId);
	        product.setDeleted(true);  // Помечаем как удалённый вместо удаления
	        productRepo.save(product);
	    } else {
	        log.info("Product ID {} updated, farmer ID {} removed", productId, farmerId);
	        productRepo.save(product);
	    }
	    
	    return removeData.toDto();
	}
    

	@Override
	public ProductDto getProduct(Long productId) {
		log.info("Fetching product with ID: {}", productId);
        return productRepo.findById(productId)
                .map(Product::toDto)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
                });
	}

	@Override
	public Set<ProductDto> getProductsByFarmer(Long farmerId) {
		log.info("Fetching products for farmer ID: {}", farmerId);
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> {
                    log.error("Farmer not found with ID: {}", farmerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
                });
        Set<ProductDto> products = farmer.getProducts().stream()
                .map(Product::toDto).collect(Collectors.toSet());
        log.debug("Found {} products for farmer ID: {}", products.size(), farmerId);
        return products;
	}

	@Override
	public Set<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice, Long productId) {
		log.info("Fetching products with price range {} - {} and ID: {}", minPrice, maxPrice, productId);
        List<Product> products = productRepo.findByPriceBetween(minPrice, maxPrice);
        Set<ProductDto> result = products.stream()
                .filter(p -> productId == null || p.getId().equals(productId))
                .map(Product::toDto)
                .collect(Collectors.toSet());
        log.debug("Found {} products in price range", result.size());
        return result;
	}

	@Override
	public List<ProductDto> getAllProducts() {
		log.info("Fetching all products");
        List<ProductDto> products = productRepo.findAll().stream()
                .map(Product::toDto)
                .collect(Collectors.toList());
        log.debug("Total products found: {}", products.size());
        return products;
        }

	@Override
    @Transactional
    public SaleRecordsDto buyProduct(Long customerId, Long productId, int quantity, UserAccount user) {
		log.info("Customer ID {} buying {} units of product ID {} by user: {}", customerId, quantity, productId, user != null ? user.getLogin() : "null");
	    if (user == null) {
	        log.error("User is not authenticated");
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
	    }
	    checkCustomerRole(user);
	    
	    log.debug("Fetching product with ID: {}", productId);
	    Product product = productRepo.findById(productId)
	            .orElseThrow(() -> {
	                log.error("Product not found with ID: {}", productId);
	                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
	            });
	    
	    log.debug("Fetching customer with ID: {}", customerId);
	    Customer customer = customerRepo.findById(customerId)
	            .orElseThrow(() -> {
	                log.error("Customer not found with ID: {}", customerId);
	                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
	            });
	    
	    log.debug("Checking if user {} matches customer ID {}", user.getLogin(), customerId);
	    if (!customer.getUserAccount().getLogin().equals(user.getLogin())) {
	        log.warn("User {} is not the customer ID {}, access denied", user.getLogin(), customerId);
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only buy as yourself");
	    }
	    
	    log.debug("Checking farmers for product ID {}", productId);
	    if (product.getFarmers() == null || product.getFarmers().isEmpty()) {
	        log.error("No farmers associated with product ID: {}", productId);
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No farmer associated with product");
	    }
	    
	    Farmer farmer = product.getFarmers().stream()
	            .findFirst()
	            .orElseThrow(() -> {
	                log.error("No farmer associated with product ID: {}", productId);
	                return new ResponseStatusException(HttpStatus.NOT_FOUND, "No farmer associated with product");
	            });
	    
	    log.debug("Checking stock for product ID {}", productId);
	    if (product.getQuantity() < quantity) {
	        log.warn("Not enough stock for product ID {}: requested {}, available {}", productId, quantity, product.getQuantity());
	        throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock available");
	    }

	    double cost = product.getPrice() * quantity;
	    log.debug("Calculated cost for purchase: {}", cost);

	    double customerBalance = customer.getBalance();
	    if (customerBalance < cost) {
	        log.warn("Insufficient funds for customer ID {}: required {}, available {}", customerId, cost, customerBalance);
	        throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Insufficient funds");
	    }
	    
	    customer.setBalance(customerBalance - cost);
	    farmer.setBalance(farmer.getBalance() + cost);
	    
	    customerRepo.saveAndFlush(customer);
	    farmerRepo.saveAndFlush(farmer);
	    
	    product.setQuantity(product.getQuantity() - quantity);
	    productRepo.saveAndFlush(product);

	    log.debug("Creating sale record for product ID {}", productId);
	    SaleRecords saleRecord = SaleRecords.builder()
	            .product(product)
	            .customer(customer)
	            .farmer(farmer)
	            .saleDate(LocalDateTime.now())
	            .saleQuantity(quantity)
	            .cost(cost)
	            .build();
	    
	    log.debug("Saving sale record for product ID {}", productId);
	    saleRecordsRepo.saveAndFlush(saleRecord);
	    
	    log.info("Product ID {} bought by customer ID {}, sale recorded with payment", productId, customerId);
	    return saleRecord.build();
    }	

	@Override
	public List<ProductDto> getSoldProducts(Long farmerId, UserAccount user) {
        log.info("Fetching sold products for farmer ID: {} by user: {}", farmerId, user.getLogin());
        checkFarmerRole(user);
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> {
                    log.error("Farmer not found with ID: {}", farmerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
                });
        if (!farmer.getUserAccount().getLogin().equals(user.getLogin())) {
            log.warn("User {} is not the owner farmer ID {}, access denied", user.getLogin(), farmerId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own sold products");
        }
        List<SaleRecords> sales = saleRecordsRepo.findByFarmerFarmerId(farmerId);
        List<ProductDto> soldProducts = sales.stream()
                .map(SaleRecords::getProduct)
                .distinct()
                .map(Product::toDto)
                .collect(Collectors.toList());
        log.debug("Found {} sold products for farmer ID: {}", soldProducts.size(), farmerId);
        return soldProducts;
    }

	@Override
	public List<SaleRecordsDto> getPurchasedProducts(Long customerId) {
		log.info("Fetching purchased products for customer ID: {}", customerId);
        customerRepo.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
        List<SaleRecordsDto> purchased = saleRecordsRepo.findByCustomerId(customerId).stream()
                .map(SaleRecords::build)
                .collect(Collectors.toList());
        log.debug("Found {} purchased records for customer ID: {}", purchased.size(), customerId);
        return purchased;
	}

	@Override
	public List<RemoveProductDataDto> getHistoryOfRemovedProducts(Long farmerId) {
		log.info("Fetching history of removed products for farmer ID: {}", farmerId);
        farmerRepo.findById(farmerId)
                .orElseThrow(() -> {
                    log.error("Farmer not found with ID: {}", farmerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
                });
        List<RemoveProductData> removedData = removeProductDataRepo.findByFarmerId(farmerId);
        List<RemoveProductDataDto> history = removedData.stream()
                .map(data -> new RemoveProductDataDto(
                        data.getProduct().toDto(), data.getSaleRecords().stream().map(SaleRecords::build) 
                                .collect(Collectors.toList()))).collect(Collectors.toList());
        log.debug("Found {} removed product records for farmer ID: {}", history.size(), farmerId);
        return history;
	}

	
	
	@Override
    @Transactional
    public SaleRecordsDto buySurpriseBag(Long customerId, Long surpriseBagId, UserAccount user) {
        log.info("Customer ID {} buying surprise bag ID {} by user: {}", customerId, surpriseBagId, 
        		user != null ? user.getLogin() : "null");
        if (user == null) {
            log.error("User is not authenticated");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
        }
        checkCustomerRole(user);

        log.debug("Fetching surprise bag with ID: {}", surpriseBagId);
        SurpriseBag surpriseBag = surpriseBagRepo.findById(surpriseBagId)
                .orElseThrow(() -> {
                    log.error("Surprise bag not found with ID: {}", surpriseBagId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Surprise bag not found");
                });

        log.debug("Fetching customer with ID: {}", customerId);
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });

        log.debug("Checking if user {} matches customer ID {}", user.getLogin(), customerId);
        if (!customer.getUserAccount().getLogin().equals(user.getLogin())) {
            log.warn("User {} is not the customer ID {}, access denied", user.getLogin(), customerId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only buy as yourself");
        }

        log.debug("Checking availability of surprise bag ID {}", surpriseBagId);
        if (!surpriseBag.isAvailable()) {
            log.warn("Surprise bag ID {} is not available", surpriseBagId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Surprise bag is not available");
        }

        if (surpriseBag.getQuantity() < 1) {
            log.warn("No surprise bags left for ID {}", surpriseBagId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No surprise bags available");
        }

        double cost = surpriseBag.getPrice();
        double customerBalance = customer.getBalance();
        if (customerBalance < cost) {
            log.warn("Insufficient funds for customer ID {}: required {}, available {}", customerId, cost, customerBalance);
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Insufficient funds");
        }

        Farmer farmer = surpriseBag.getFarmers().stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No farmer associated with surprise bag ID: {}", surpriseBagId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "No farmer associated with surprise bag");
                });

        customer.setBalance(customerBalance - cost);
        farmer.setBalance(farmer.getBalance() + cost);
        customerRepo.saveAndFlush(customer);
        farmerRepo.saveAndFlush(farmer);

        surpriseBag.setQuantity(surpriseBag.getQuantity() - 1);
        surpriseBagRepo.saveAndFlush(surpriseBag);

        SaleRecords saleRecord = SaleRecords.builder()
                .product(null)  // SurpriseBag не является обычным продуктом
                .customer(customer)
                .farmer(farmer)
                .saleDate(LocalDateTime.now())
                .saleQuantity(1)
                .cost(cost)
                .build();
        saleRecordsRepo.saveAndFlush(saleRecord);

        log.info("Surprise bag ID {} bought by customer ID {}, sale recorded with payment", surpriseBagId, customerId);
        return saleRecord.build();
    }

    @Override
    @Transactional
    public SurpriseBag createSurpriseBag(LocalDateTime startTime, LocalDateTime endTime, int quantity, UserAccount user) {
        log.info("Creating surprise bag by user: {}", user != null ? user.getLogin() : "null");
        if (user == null) {
            log.error("User is not authenticated");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
        }
        checkFarmerRole(user);

        Farmer farmer = farmerRepo.findByUserAccountLogin(user.getLogin())
                .orElseThrow(() -> {
                    log.error("Farmer profile not found for user: {}", user.getLogin());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer profile not found");
                });

        // Создание новой сумки-сюрприза
        SurpriseBag surpriseBag = SurpriseBag.builder()
                .name("Surprise Bag")  // Название по умолчанию
                .quantity(quantity)
                .price(5.0)  // Фиксированная цена по умолчанию
                .startTime(startTime)
                .endTime(endTime)
                .imgUrl("http://example.com/surprise_bag.jpg")  // Пример URL, можно сделать параметром
                .farmers(Collections.singletonList(farmer))  // Привязываем фермера
                .build();

        // Сохранение в базе
        surpriseBagRepo.save(surpriseBag);
        log.info("Surprise bag created with ID: {} by farmer: {}", surpriseBag.getId(), farmer.getFarmerId());

        return surpriseBag;
    }

    @Override
    public List<SurpriseBagDto> getAvailableSurpriseBags() {
    	    log.info("Fetching available surprise bags");
    	    List<SurpriseBagDto> bags = surpriseBagRepo.findAll().stream()
    	            .filter(SurpriseBag::isAvailable)
    	            .map(SurpriseBag::build)
    	            .collect(Collectors.toList());
    	    log.debug("Found {} available surprise bags", bags.size());
    	    return bags;
    }
    
    @Scheduled(fixedRate = 3600000)  // Выполняется каждый час (3600000 мс = 1 час)
    @Transactional
    @Override
    public void cleanUpExpiredSurpriseBags() {
        log.info("Cleaning up expired surprise bags");
        LocalDateTime now = LocalDateTime.now();
        List<SurpriseBag> expiredBags = surpriseBagRepo.findAll().stream()
                .filter(bag -> bag.getEndTime().isBefore(now))
                .collect(Collectors.toList());

        if (!expiredBags.isEmpty()) {
            surpriseBagRepo.deleteAll(expiredBags);
            log.info("Deleted {} expired surprise bags", expiredBags.size());
        } else {
            log.debug("No expired surprise bags found");
        }

    }}
