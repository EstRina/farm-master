package farming.products.controllers;

import farming.accounting.entity.UserAccount;
import farming.products.dto.ProductDto;
import farming.products.dto.RemoveProductDataDto;
import farming.products.dto.SaleRecordsDto;
import farming.products.dto.SurpriseBagDto;
import farming.products.entity.SurpriseBag;
import farming.products.service.IProductsService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

	private final IProductsService productService;

	public ProductController(IProductsService productService) {
		this.productService = productService;
	}

	@PostMapping("/add")
	public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto,
			@AuthenticationPrincipal UserAccount user) {
		log.debug("Received addProduct request with product: {} and user: {}",
				productDto != null ? productDto.getProductName() : "null", user != null ? user.getLogin() : "null");
		if (user == null) {
			log.error("User is not authenticated");
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
		}
		ProductDto result = productService.addProduct(productDto, user);
		return ResponseEntity.ok(result);
	}

	@PutMapping("/update")
	public ResponseEntity<Boolean> updateProduct(@RequestBody ProductDto productDto,
			@AuthenticationPrincipal UserAccount user) {
		log.info("Updating product ID: {} by user: {}", productDto.getProductId(),
				user != null ? user.getLogin() : "null");
		boolean result = productService.updateProduct(productDto, user);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId) {
		log.info("Fetching product ID: {}", productId);
		ProductDto product = productService.getProduct(productId);
		return ResponseEntity.ok(product);
	}

	@GetMapping("/byFarmer/{farmerId}")
	public ResponseEntity<Set<ProductDto>> getProductsByFarmer(@PathVariable Long farmerId) {
		log.info("Fetching products for farmer ID: {}", farmerId);
		Set<ProductDto> products = productService.getProductsByFarmer(farmerId);
		return ResponseEntity.ok(products);
	}

	@GetMapping("/priceRange")
	public ResponseEntity<Set<ProductDto>> getProductsByPriceRange(@RequestParam double minPrice,
			@RequestParam double maxPrice, @RequestParam(required = false) Long productId) {
		log.info("Fetching products in price range {} - {}, product ID: {}", minPrice, maxPrice, productId);
		Set<ProductDto> products = productService.getProductsByPriceRange(minPrice, maxPrice, productId);
		return ResponseEntity.ok(products);
	}

	@GetMapping("/all")
	public ResponseEntity<List<ProductDto>> getAllProducts() {
		log.info("Fetching all products");
		List<ProductDto> products = productService.getAllProducts();
		return ResponseEntity.ok(products);
	}

	@PostMapping("/buy")
	public ResponseEntity<SaleRecordsDto> buyProduct(@RequestParam Long customerId, @RequestParam Long productId,
			@RequestParam int quantity, @AuthenticationPrincipal UserAccount user) {
		log.info("Buying product ID {} for customer ID {}, quantity: {} by user: {}", productId, customerId, quantity,
				user != null ? user.getLogin() : "null");
		SaleRecordsDto sale = productService.buyProduct(customerId, productId, quantity, user);
		return ResponseEntity.ok(sale);
	}

	@GetMapping("/sold/{farmerId}")
	public ResponseEntity<List<ProductDto>> getSoldProducts(@PathVariable Long farmerId,
			@AuthenticationPrincipal UserAccount user) {
		log.info("Fetching sold products for farmer ID: {} by user: {}", farmerId,
				user != null ? user.getLogin() : "null");
		List<ProductDto> products = productService.getSoldProducts(farmerId, user);
		return ResponseEntity.ok(products);
	}

	@GetMapping("/purchased/{customerId}")
	public ResponseEntity<List<SaleRecordsDto>> getPurchasedProducts(@PathVariable Long customerId) {
		log.info("Fetching purchased products for customer ID: {}", customerId);
		List<SaleRecordsDto> purchases = productService.getPurchasedProducts(customerId);
		return ResponseEntity.ok(purchases);
	}

	@GetMapping("/history/{farmerId}")
	public ResponseEntity<List<RemoveProductDataDto>> getHistoryOfRemovedProducts(@PathVariable Long farmerId) {
		log.info("Fetching history of removed products for farmer ID: {}", farmerId);
		List<RemoveProductDataDto> history = productService.getHistoryOfRemovedProducts(farmerId);
		return ResponseEntity.ok(history);
	}

	@DeleteMapping("/remove")
	public ResponseEntity<RemoveProductDataDto> removeProduct(@RequestParam Long productId, @RequestParam Long farmerId,
			@AuthenticationPrincipal UserAccount user) {
		log.info("Removing product ID {} for farmer ID {} by user: {}", productId, farmerId,
				user != null ? user.getLogin() : "null");
		RemoveProductDataDto removed = productService.removeProduct(productId, farmerId, user);
		return ResponseEntity.ok(removed);
	}
	
	@PostMapping("/surprise-bag/buy")
    public ResponseEntity<SaleRecordsDto> buySurpriseBag(
            @RequestParam Long customerId,
            @RequestParam Long surpriseBagId,
            @AuthenticationPrincipal UserAccount user) {
        log.info("Buying surprise bag ID {} for customer ID {} by user: {}", surpriseBagId, customerId, user != null ? user.getLogin() : "null");
        SaleRecordsDto sale = productService.buySurpriseBag(customerId, surpriseBagId, user);
        return ResponseEntity.ok(sale);
    }

	@PostMapping("/surprise-bag/create")
	public ResponseEntity<SurpriseBagDto> createSurpriseBag(
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
	        @RequestParam int quantity,
	        @AuthenticationPrincipal UserAccount user) {
	    log.info("Creating surprise bag by user: {}", user != null ? user.getLogin() : "null");
	    SurpriseBag surpriseBag = productService.createSurpriseBag(startTime, endTime, quantity, user);
	    return ResponseEntity.ok(surpriseBag.build());
    }

    @GetMapping("/surprise-bag/available")
    public ResponseEntity<List<SurpriseBagDto>> getAvailableSurpriseBags() {
        log.info("Fetching available surprise bags");
        List<SurpriseBagDto> bags = productService.getAvailableSurpriseBags();
        return ResponseEntity.ok(bags);
    }
}