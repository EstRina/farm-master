package farming.farmer.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import farming.accounting.entity.UserAccount;
import farming.farmer.dto.AddressDto;
import farming.farmer.dto.FarmerDto;
import farming.products.entity.Product;
import farming.products.entity.SurpriseBag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "farmers")
public class Farmer  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long farmerId;

//	String firstName;
//	String lastName;
	String email;
	
	@OneToOne
    @JoinColumn(name = "login", referencedColumnName = "login")
    UserAccount userAccount;
	
	String phone;

	@Embedded
	Address address;
	
	@OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();  // Каждый фермер имеет свои продукты

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurpriseBag> surpriseBags = new ArrayList<>();  // Каждый фермер имеет свои сумки-сюрпризы
	
		public static Farmer of(FarmerDto dto) {
			return Farmer.builder()
	                .farmerId(dto.getFarmerId()).phone(dto.getPhone())
	                .address(dto.getAddress() != null ? new Address(dto.getAddress().getCountry(), dto.getAddress().getCity(), 
	                		dto.getAddress().getStreet()) : null)
	                .build();
	}
	

		public FarmerDto build() {
		    return FarmerDto.builder()
		            .farmerId(farmerId)
		            .firstName(userAccount != null ? userAccount.getFirstName() : null)
		            .lastName(userAccount != null ? userAccount.getLastName() : null)
		            .phone(phone)
		            .address(address != null ? new AddressDto(address.getCountry(), address.getCity(), address.getStreet()) : null)
		            .build();
	    }

		public void addProduct(Product product) {
	        products.add(product);
	        product.setFarmer(this);
	    }

	    public void addSurpriseBag(SurpriseBag surpriseBag) {
	        surpriseBags.add(surpriseBag);
	        surpriseBag.setFarmer(this);
	    }
		
}
