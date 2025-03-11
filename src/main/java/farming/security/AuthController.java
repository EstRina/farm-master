package farming.security;

import farming.accounting.dto.UserRequestDto;
import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;
import farming.accounting.service.AccountingManagement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AccountingManagement accountingService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(
            @RequestBody UserRequestDto authRequest) {
        log.info("Authenticating user: {}", authRequest.getLogin());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getLogin());
        final String jwt = jwtUtil.generateToken(userDetails);
        log.info("User {} authenticated successfully", authRequest.getLogin());
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register/farmer")
    public ResponseEntity<UserResponseDto> registerFarmer(@RequestBody UserRequestDto userDto) {
        log.info("Registering farmer with login: {}", userDto.getLogin());
        UserResponseDto response = accountingService.registration(userDto, UserType.FARMER);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/customer")
    public ResponseEntity<UserResponseDto> registerCustomer(@RequestBody UserRequestDto userDto) {
        log.info("Registering customer with login: {}", userDto.getLogin());
        UserResponseDto response = accountingService.registration(userDto, UserType.CUSTOMER);
        return ResponseEntity.ok(response);
    }
    
}
