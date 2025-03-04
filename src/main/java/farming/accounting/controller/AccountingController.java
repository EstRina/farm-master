package farming.accounting.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import farming.accounting.dto.UserRequestDto;
import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;
import farming.accounting.entity.UserAccount;
import farming.accounting.service.IAccountingManagement;

@RestController
@RequestMapping("/api")
public class AccountingController{

	@Autowired
	IAccountingManagement service;

	@PostMapping({"/auth/register", "/auth/register/"})
	public UserResponseDto registration(@RequestBody UserRequestDto userDto) {
		return service.registration(userDto, userDto.getUserType());
	}

	@GetMapping("/user/me")
	public UserResponseDto getUser(Principal principal) { 
		return service.getUser(principal.getName());
	}

	@PutMapping("/user/{login}")
	public UserResponseDto editUser(@RequestBody UserResponseDto user, @PathVariable String login) {
		return service.editUser(user, login);
	}

	@PutMapping("/password")
	public boolean updatePassword(Principal principal, @RequestHeader("X-New-Password") String password) {
		return service.updatePassword(principal.getName(), password);
	}

	@GetMapping("/userType/{login}")
	public UserType getUserType(@PathVariable String login) {
		return service.getUserType(login);
				}

	@DeleteMapping("/user/{login}")
	public UserResponseDto removeUser(@PathVariable String login) {
		return service.removeUser(login);
	}

	@PutMapping("/revoke/{login}")
	public boolean revokeAccount(@PathVariable String login) {
		return service.revokeAccount(login);
	}

	@PutMapping("/activate/{login}")
	public boolean activateAccount(@PathVariable String login) {
		return service.activateAccount(login);
	}

	@GetMapping("/password/{login}")
	public String getPasswordHash(@PathVariable String login) {
		return service.getPasswordHash(login);
	}

	@GetMapping("/activation_date/{login}")
	public LocalDateTime getActivationDate(@PathVariable String login) {
	return service.getActivationDate(login);
	}
	
	@GetMapping("/home")
	public String home(@AuthenticationPrincipal UserAccount user) {
	    if (user == null) {
	        return "Welcome, guest! Please log in.";
	    }
	    return "Welcome, " + user.getFirstName() + "! Your role: " + user.getUserType();
	}

    @GetMapping("/admin/test")
    public String adminTest() {
        return "Admin access granted";
    }

    @GetMapping("/farmer/test")
    public String farmerTest() {
        return "Farmer access granted";
    }

    @GetMapping("/customer/test")
    public String customerTest() {
        return "Customer access granted";
    }

}
