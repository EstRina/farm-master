package farming.products.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import farming.accounting.entity.UserAccount;
import farming.products.dto.ProductDto;
import farming.products.dto.RemoveProductDataDto;
import farming.products.dto.SaleRecordsDto;
import farming.products.dto.SurpriseBagDto;
import farming.products.entity.SurpriseBag;

public interface IProductsService {

	ProductDto addProduct(ProductDto productDto, UserAccount user);
	boolean updateProduct(ProductDto productDto, UserAccount user);
    RemoveProductDataDto removeProduct(Long productId, Long farmerId, UserAccount user);
	
	ProductDto getProduct(Long productId);
	Set<ProductDto> getProductsByFarmer(Long farmerId);
	Set<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice, Long productId);
	List<ProductDto> getAllProducts();
	
	SaleRecordsDto buyProduct(Long customerId, Long productId, int quantity, UserAccount user);
	List<ProductDto> getSoldProducts(Long farmerId, UserAccount user);
	List<SaleRecordsDto> getPurchasedProducts(Long customerId);
	List<RemoveProductDataDto> getHistoryOfRemovedProducts(Long farmerId);
	
	SaleRecordsDto buySurpriseBag(Long customerId, Long surpriseBagId, UserAccount user);
	SurpriseBag createSurpriseBag(LocalDateTime startTime, LocalDateTime endTime, int quantity, UserAccount user);
	List<SurpriseBagDto> getAvailableSurpriseBags();
	void cleanUpExpiredSurpriseBags();
	
	
}
