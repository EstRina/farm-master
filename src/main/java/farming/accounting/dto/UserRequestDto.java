package farming.accounting.dto;

import farming.farmer.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserRequestDto {

	
	private String login;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;       // Добавлено phone
    private AddressDto address; // Адрес
	private UserType userType;
}
