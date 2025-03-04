package farming.accounting.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;
import farming.farmer.entity.Address;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Entity
@Table(name = "user-accounts")
public class UserAccount implements UserDetails{
	
	@Id
@Setter(value = AccessLevel.NONE)
	private String login;
	
	private String hash;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;       
	
	@Embedded
    private Address address;
    
	@Enumerated(EnumType.STRING)
	private UserType userType;
	
	private LocalDateTime activationDate;
	private boolean revoked;
	private LinkedList<String> lastHash = new LinkedList<String>();
	
	public UserAccount(String login, String hash, String firstName, String lastName, UserType userType, 
			String email, String phone, Address address) {
		super();
		this.login = login;
        this.hash = hash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.activationDate = LocalDateTime.now();
        this.revoked = false;
		}
	
	public UserResponseDto build() {
		UserResponseDto dto = new UserResponseDto();
		dto.setLogin(login);
		dto.setFirstName(firstName);
		dto.setLastName(lastName);
		dto.setUserType(userType);
		dto.setEmail(email);
		dto.setPhone(phone);
		dto.setAddress(address);
		return dto;
	}

	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("TYPE_" + userType.name()));
    }

    @Override
    public String getPassword() { return hash; }

    @Override
    public String getUsername() { return login; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !revoked; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return !revoked; }


}
