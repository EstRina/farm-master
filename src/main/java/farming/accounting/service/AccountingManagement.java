package farming.accounting.service;

import java.time.LocalDateTime;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import farming.accounting.dto.UserRequestDto;
import farming.accounting.dto.UserResponseDto;
import farming.accounting.dto.UserType;
import farming.accounting.dto.exceptions.AccountActivateException;
import farming.accounting.dto.exceptions.AccountRevokeException;
import farming.accounting.dto.exceptions.PasswordValidException;
import farming.accounting.dto.exceptions.UserExistsException;
import farming.accounting.dto.exceptions.UserNotFoundException;
import farming.accounting.entity.UserAccount;
import farming.accounting.repo.UserRepository;
import farming.customer.service.CustomerService;
import farming.farmer.entity.Address;
import farming.farmer.service.FarmerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingManagement implements IAccountingManagement, CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final FarmerService farmerService; 
	private final CustomerService customerService;

	@Value("${password.length:5}")
	private int passwordLength;

	@Value("${last_hash:3}")
	private int n_last_hash;
	
	@Override
	@Transactional
	public UserResponseDto registration(UserRequestDto userDto, UserType userType) {
		log.info("Registering user with login: {}", userDto.getLogin());
	    String password = userDto.getPassword();
	    String login = userDto.getLogin();
	    if (!isPasswordValid(password)) {
	        log.error("Invalid password for login: {}", login);
	        throw new PasswordValidException(password);
	    }
	    if (userRepository.existsById(login)) {
	        log.warn("User already exists: {}", login);
	        throw new UserExistsException(login);
	    }
	    Address address = userDto.getAddress() != null 
	        ? new Address(userDto.getAddress().getCountry(), userDto.getAddress().getCity(), userDto.getAddress().getStreet()) 
	        : null;
	    UserAccount user = new UserAccount(login, passwordEncoder.encode(password), userDto.getFirstName(),
	            userDto.getLastName(), userType, userDto.getEmail(), userDto.getPhone(), address);
	    userRepository.save(user);
	    if (userType == UserType.FARMER) {
	        log.debug("Creating farmer profile for user: {}", login);
	        farmerService.createFarmerProfile(user);
	    } else if (userType == UserType.CUSTOMER) {
	        log.debug("Creating customer profile for user: {}", login);
	        customerService.createCustomerProfile(user);
	    }
	    log.info("User registered: {}", login);
	    return user.build();
	}

	@Override
	public UserResponseDto getUser(String login) {
		log.info("Fetching user with login: {}", login);
		UserAccount user = getUserAccount(login);
		return user.build();
	}

	@Override
	@Transactional
	public UserResponseDto editUser(UserResponseDto user, String login) {
		UserAccount account = getUserAccount(login);
		if (user.getFirstName() != null)
			account.setFirstName(user.getFirstName());
		if (user.getLastName() != null)
			account.setLastName(user.getLastName());
		userRepository.save(account);
		return account.build();
	}

	@Override
	@Transactional
	public boolean updatePassword(String login, String newPassword) {

		log.info("Updating password for login: {}", login);
        if (newPassword == null || !isPasswordValid(newPassword)) {
            log.error("Invalid password: {}", newPassword);
            throw new PasswordValidException(newPassword);
        }

        UserAccount user = getUserAccount(login);

        if (passwordEncoder.matches(newPassword, user.getHash())) {
            log.warn("New password matches current password for login: {}", login);
            throw new PasswordValidException(newPassword);
        }

        LinkedList<String> lastHash = user.getLastHash();
        if (lastHash == null) {
            lastHash = new LinkedList<>();
            user.setLastHash(lastHash);
        }
        if (isPasswordFromLast(newPassword, lastHash)) {
            log.warn("New password matches a previous password for login: {}", login);
            throw new PasswordValidException(newPassword);
        }

        if (lastHash.size() >= n_last_hash) {
            lastHash.removeFirst();
        }
        lastHash.add(user.getHash());

        user.setHash(passwordEncoder.encode(newPassword));
        user.setActivationDate(LocalDateTime.now());
        userRepository.save(user);
        log.info("Password updated successfully for login: {}", login);
        return true;
	}

	private boolean isPasswordValid(String password) {
		return password.length() >= passwordLength;
	}

	private boolean isPasswordFromLast(String newPassword, LinkedList<String> lastHash) {
		return lastHash.stream().anyMatch(h -> passwordEncoder.matches(newPassword, h));
	}

	@Override
	@Transactional
	public UserResponseDto removeUser(String login) {
		log.info("Removing user with login: {}", login);
	    UserAccount user = getUserAccount(login);
	    UserResponseDto response = user.build();
	    userRepository.delete(user);
	    return response;
	}

	@Override
	@Transactional
	public boolean revokeAccount(String login) {
		log.info("Revoking account with login: {}", login);
	    UserAccount user = getUserAccount(login);
	    if (user.isRevoked()) {
	        log.warn("Account already revoked: {}", login);
	        throw new AccountRevokeException(login);
	    }
	    user.setRevoked(true);
	    userRepository.save(user);
	    log.info("Account revoked: {}", login);
	    return true;
	}

	@Override
	@Transactional
	public boolean activateAccount(String login) {
		log.info("Activating account with login: {}", login);
	    UserAccount user = getUserAccount(login);
	    if (!user.isRevoked()) {
	        log.warn("Account already active: {}", login);
	        throw new AccountActivateException(login);
	    }
	    user.setRevoked(false);
	    user.setActivationDate(LocalDateTime.now());
	    userRepository.save(user);
	    log.info("Account activated: {}", login);
	    return true;
	}
	
	@Override
	public UserType getUserType(String login) {
		log.info("Fetching user type for login: {}", login);
        return userRepository.findById(login)
                .map(UserAccount::getUserType)
                .orElseThrow(() -> new UserNotFoundException(login));
	}


	private UserAccount getUserAccount(String login) {
		UserAccount user = userRepository.findById(login).orElseThrow(() -> new UserNotFoundException(login));
		return user;
	}
	
	@Override
	public String getPasswordHash(String login) {
		log.info("Fetching password hash for login: {}", login);
        UserAccount user = getUserAccount(login);
        return user.getHash();
	}

	@Override
	public LocalDateTime getActivationDate(String login) {
		log.info("Fetching activation date for login: {}", login);
        UserAccount user = getUserAccount(login);
        return user.getActivationDate();
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		log.info("Checking for admin user...");
	    if (!userRepository.existsById("admin")) {
	        log.info("Creating admin user...");
	        UserAccount admin = new UserAccount("admin", passwordEncoder.encode("admin"), "Admin", "Admin", 
	        		UserType.ADMIN, "admin@farm.com", null, null);
	        userRepository.save(admin);
	        log.info("Admin user created.");
	    } else {
	        log.info("Admin user already exists.");
	    }
	    }
	}

	

