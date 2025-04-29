package farming.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import farming.accounting.entity.UserAccount;
import farming.accounting.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Looking up user: {}", username);
        UserAccount user = userRepository.findById(username)
                .orElseThrow(() -> {
                    log.error("Failed to find user '{}'", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
        if (user.isRevoked()) {
            throw new UsernameNotFoundException("User account is revoked: " + username);
        }
        return user;
    }
	
}
