package farming.farmer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class FarmerDto {
	
	Long farmerId;
//	String login;
//	String password;
	String firstName;
	String lastName;
//	String email;
	String phone;
	AddressDto address;

}
