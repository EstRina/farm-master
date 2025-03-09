package farming.products.entity;

import java.util.ArrayList;
import java.util.List;

import farming.farmer.dto.FarmerDto;
import farming.farmer.entity.Farmer;
import farming.products.dto.ProductDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
	public String productName;
	public int quantity;
	public Double price;
	public String imgUrl;

	@ManyToOne
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer; 
	
	private boolean deleted = false;
	
	public static Product of(ProductDto dto) {
		return Product.builder()
	            .id(dto.getProductId())
	            .productName(dto.getProductName())
	            .quantity(dto.getQuantity())
	            .price(dto.getPrice())
	            .imgUrl(dto.getImgUrl())
	            .build();
    }

	public ProductDto toDto() {
        return ProductDto.builder()
                .productId(id)
                .productName(productName)
                .quantity(quantity)
                .price(price)
                .imgUrl(imgUrl)
                .farmer(farmer != null ? farmer.build() : null)
                .build();
    }
}
