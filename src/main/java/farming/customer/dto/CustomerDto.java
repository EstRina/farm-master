package farming.customer.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CustomerDto {

	@Id
	Long customerId;
    String login;
    String firstName;
    String lastName;
    String email;
    double balance;
}
