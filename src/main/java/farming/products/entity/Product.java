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

	@ManyToMany
	 @JoinTable(name = "farmers_products", 
	 			joinColumns = @JoinColumn(name = "product_id"),
		        inverseJoinColumns = @JoinColumn(name = "farmer_id"))
	List<Farmer> farmers = new ArrayList<>();
	
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
        FarmerDto farmerDto = null;
        if (!farmers.isEmpty()) {
            farmerDto = farmers.get(0).build(); 
        }
        return ProductDto.builder()
                .productId(id)
                .productName(productName)
                .quantity(quantity)
                .price(price)
                .imgUrl(imgUrl)
                .farmer(farmerDto)  
                .build();
    }
}
