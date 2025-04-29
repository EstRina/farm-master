package farming.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import farming.accounting.entity.UserAccount;
import farming.accounting.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserRepository userRepository;
	private final JwtRequestFilter jwtRequestFilter;
    private final CustomUserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults()).csrf(csrf -> csrf.disable()) // Отключаем CSRF (можно включить при
																				// необходимости для форм)

				// Настройка авторизации запросов
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/register/farmer", "/api/auth/register/customer", "/api/auth/login").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("TYPE_ADMIN")
                .requestMatchers("/api/farmer/**", "/products/add", "/products/update", "/products/remove",
                        "/products/sold/{farmerId}", "/products/surprise-bag/create").hasAuthority("TYPE_FARMER")
                .requestMatchers("/api/customer/**", "/products/surprise-bag/buy", "/purchased/{customerId}").hasAuthority("TYPE_CUSTOMER")
                .anyRequest().authenticated()
        )
        .exceptionHandling(exceptions -> exceptions.accessDeniedPage("/access-denied"))
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}