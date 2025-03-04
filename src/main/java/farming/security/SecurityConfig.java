package farming.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import farming.accounting.entity.UserAccount;
import farming.accounting.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private final UserRepository userRepository;

	public SecurityConfig(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults()).csrf(csrf -> csrf.disable()) // Отключаем CSRF (можно включить при
																				// необходимости для форм)

				// Настройка авторизации запросов
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/auth/register", "/api/login")
						.permitAll().requestMatchers("/api/admin/**").hasAuthority("TYPE_ADMIN")
						.requestMatchers("/api/farmer/**", "/products/add", "/products/update", "/products/remove", 
								"/products/sold/{farmerId}", "/products/surprise-bag/create").hasAuthority("TYPE_FARMER")
						.requestMatchers("/api/customer/**", "/products/surprise-bag/buy", "/purchased/{customerId}").hasAuthority("TYPE_CUSTOMER").anyRequest().authenticated())

				// Настройка формы логина
				.formLogin(form -> form.loginProcessingUrl("/login").defaultSuccessUrl("/api/home", true).permitAll())

				// Настройка logout
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())

				// Обработка исключений
				.exceptionHandling(exceptions -> exceptions.accessDeniedPage("/access-denied"));

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
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
	    };

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}