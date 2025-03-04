package farming.products.dto;

import farming.farmer.dto.FarmerDto;
import farming.products.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ProductDto {

	Long productId;
	String productName;
	int quantity;
	Double price;
	String imgUrl;
	FarmerDto farmer;

	public static ProductDto of(Product product) {
		return ProductDto.builder().productId(product.getId()).productName(product.getProductName()).quantity(product.getQuantity()).price(product.getPrice()).imgUrl(product.getImgUrl()).build();
	}
	
}
