package farming.farmer.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails.Address;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import farming.accounting.entity.UserAccount;
import farming.farmer.dto.FarmerDto;
import farming.farmer.entity.Farmer;
import farming.farmer.repo.FarmerRepository;
import farming.products.entity.Product;
import farming.products.repo.ProductsRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FarmerService implements IFarmerService{
	
	@Autowired
	FarmerRepository farmerRepo;
	@Autowired
	ProductsRepository productRepo;
	
	public FarmerService(FarmerRepository farmerRepo, ProductsRepository productRepo) {
        this.farmerRepo = farmerRepo;
        this.productRepo = productRepo;
    }

	@Override
	public FarmerDto getFarmer(Long farmerId) {
//		return farmerRepo.findById(farmerId).map(Farmer::build).orElseThrow(() -> 
//		new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
		log.info("Fetching farmer with ID: {}", farmerId);
		return farmerRepo.findById(farmerId).map(farmer -> {log.debug("Farmer found: {}", farmer);
					return farmer.build();
				})
				.orElseThrow(() -> {log.error("Farmer not found with ID: {}", farmerId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
				});
	}

	@Override
	public Set<FarmerDto> getFarmersByProduct(Long productId) {
//		Product product = productRepo.findById(productId).orElseThrow(() ->
//		new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
//		return product.getFarmers().stream().map(Farmer::build).collect(Collectors.toSet());
		log.info("Fetching farmers for product ID: {}", productId);
		Product product = productRepo.findById(productId)
				.orElseThrow(() -> {
					log.error("Product not found with ID: {}", productId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
				});
		Set<FarmerDto> farmers = product.getFarmers().stream()
				.map(Farmer::build)
				.collect(Collectors.toSet());
		log.debug("Found {} farmers for product ID: {}", farmers.size(), productId);
		return farmers;
	}

	@Override
	public List<FarmerDto> getAllFarmers() {
//		return farmerRepo.findAll().stream().map(Farmer::build).collect(Collectors.toList());
		log.info("Fetching all farmers");
		List<FarmerDto> farmers = farmerRepo.findAll().stream()
				.map(Farmer::build)
				.collect(Collectors.toList());
		log.debug("Total farmers found: {}", farmers.size());
		return farmers;
	}

	@Override
    @Transactional
    public void createFarmerProfile(UserAccount user) {
//        if (farmerRepo.existsByUserAccountLogin(user.getLogin())) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "Farmer profile already exists for login: " + user.getLogin());
//        }
//        Farmer farmer = Farmer.builder()
//                .userAccount(user)
//                .balance(0.0) // Начальный баланс
//                .build();
//        farmerRepo.save(farmer);
		log.info("Creating farmer profile for user: {}", user.getLogin());
		if (farmerRepo.existsByUserAccountLogin(user.getLogin())) {
			log.warn("Farmer profile already exists for login: {}", user.getLogin());
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Farmer profile already exists for login: " + user.getLogin());
		}
		Farmer farmer = Farmer.builder()
				.userAccount(user)
				.phone(user.getPhone())    
                .email(user.getEmail())
                .address(user.getAddress())
				.balance(0.0)
				.build();
		farmerRepo.save(farmer);
		log.info("Farmer profile created for login: {} with phone: {}, email: {}, address: {}", 
                user.getLogin(), farmer.getPhone(), farmer.getEmail(), 
                farmer.getAddress() != null ? farmer.getAddress().toString() : "null");
    }
	
	@Override
	@Transactional
    public FarmerDto updateFarmer(Long farmerId, FarmerDto dto) {
//        Farmer farmer = farmerRepo.findById(farmerId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
//        if (dto.getPhone() != null) farmer.setPhone(dto.getPhone());
////        if (dto.getAddress() != null) farmer.setAddress(dto.getAddress());
//        if (dto.getBalance() != null) farmer.setBalance(dto.getBalance());
//        farmerRepo.save(farmer);
//        return farmer.build();
		log.info("Updating farmer with ID: {}", farmerId);
		Farmer farmer = farmerRepo.findById(farmerId)
				.orElseThrow(() -> {
					log.error("Farmer not found with ID: {}", farmerId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
				});
		if (dto.getPhone() != null) {
			farmer.setPhone(dto.getPhone());
			log.debug("Updated phone for farmer ID {}: {}", farmerId, dto.getPhone());
		}
		if(dto.getAddress() != null) {
			farmer.setAddress(new farming.farmer.entity.Address(dto.getAddress().getCountry(), 
					dto.getAddress().getCity(), dto.getAddress().getStreet()));
			log.debug("Updated address for farmer ID {}: {}", farmerId, dto.getAddress());
		}
			if (dto.getBalance() != null) {
			farmer.setBalance(dto.getBalance());
			log.debug("Updated balance for farmer ID {}: {}", farmerId, dto.getBalance());
		}
		farmerRepo.save(farmer);
		log.info("Farmer updated successfully: {}", farmerId);
		return farmer.build();
    }

	@Override
    @Transactional
    public void deleteFarmer(Long farmerId) {
//        Farmer farmer = farmerRepo.findById(farmerId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
//        farmerRepo.delete(farmer);
		log.info("Deleting farmer with ID: {}", farmerId);
		Farmer farmer = farmerRepo.findById(farmerId)
				.orElseThrow(() -> {
					log.error("Farmer not found with ID: {}", farmerId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
				});
		farmerRepo.delete(farmer);
		log.info("Farmer deleted: {}", farmerId);
    }
	
	@Override
	@Transactional
	public void addProductToFarmer(Long farmerId, Long productId) {
//	    Farmer farmer = farmerRepo.findById(farmerId)
//	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
//	    Product product = productRepo.findById(productId)
//	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
//	    farmer.getProducts().add(product);
//	    farmerRepo.save(farmer);
		log.info("Adding product ID {} to farmer ID {}", productId, farmerId);
		Farmer farmer = farmerRepo.findById(farmerId)
				.orElseThrow(() -> {
					log.error("Farmer not found with ID: {}", farmerId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
				});
		Product product = productRepo.findById(productId)
				.orElseThrow(() -> {
					log.error("Product not found with ID: {}", productId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
				});
		farmer.getProducts().add(product);
		farmerRepo.save(farmer);
		productRepo.save(product);
		log.info("Product ID {} added to farmer ID {}", productId, farmerId);
	}

	@Override
	@Transactional
	public void removeProductFromFarmer(Long farmerId, Long productId) {
//	    Farmer farmer = farmerRepo.findById(farmerId)
//	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
//	    Product product = productRepo.findById(productId)
//	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
//	    farmer.getProducts().remove(product);
//	    farmerRepo.save(farmer);
		log.info("Removing product ID {} from farmer ID {}", productId, farmerId);
		Farmer farmer = farmerRepo.findById(farmerId)
				.orElseThrow(() -> {
					log.error("Farmer not found with ID: {}", farmerId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
				});
		Product product = productRepo.findById(productId)
				.orElseThrow(() -> {
					log.error("Product not found with ID: {}", productId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
				});
		farmer.getProducts().remove(product);
		farmerRepo.save(farmer);
		log.info("Product ID {} removed from farmer ID {}", productId, farmerId);
	}

	@Override
	public Double getFarmerBalance(Long farmerId) {
//	    return farmerRepo.findById(farmerId)
//	            .map(Farmer::getBalance)
//	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found"));
		log.info("Fetching balance for farmer ID: {}", farmerId);
		return farmerRepo.findById(farmerId)
				.map(farmer -> {
					log.debug("Balance for farmer ID {}: {}", farmerId, farmer.getBalance());
					return farmer.getBalance();
				})
				.orElseThrow(() -> {
					log.error("Farmer not found with ID: {}", farmerId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
				});
	}

}
