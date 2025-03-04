package farming.customer.entity;

import farming.accounting.entity.UserAccount;
import farming.customer.dto.CustomerDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Entity
@Builder
@Table(name = "customers")
public class Customer {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "login", referencedColumnName = "login")
    private UserAccount userAccount;

    private double balance;

    public static Customer of(CustomerDto dto) {
        return Customer.builder()
                .id(dto.getCustomerId())
                .balance(dto.getBalance())
                .build();
    }

    public CustomerDto build() {
        return CustomerDto.builder()
                .customerId(id)
                .login(userAccount != null ? userAccount.getLogin() : null)
                .firstName(userAccount != null ? userAccount.getFirstName() : null)
                .lastName(userAccount != null ? userAccount.getLastName() : null)
                .email(userAccount != null ? userAccount.getEmail() : null)
                .balance(balance)
                .build();
    }
}