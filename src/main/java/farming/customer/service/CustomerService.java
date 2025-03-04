package farming.customer.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import farming.accounting.entity.UserAccount;
import farming.customer.dto.CustomerDto;
import farming.customer.entity.Customer;
import farming.customer.repo.CustomerRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService implements ICustomerService{

	@Autowired
	private CustomerRepository customerRepo;

    @Override
    @Transactional
    public void createCustomerProfile(UserAccount user) {
        log.info("Creating customer profile for user: {}", user.getLogin());
        if (customerRepo.existsByUserAccountLogin(user.getLogin())) {
            log.warn("Customer profile already exists for login: {}", user.getLogin());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer profile already exists for login: " + user.getLogin());
        }
        Customer customer = Customer.builder()
                .userAccount(user)
                .balance(0.0)  
                .build();
        customerRepo.save(customer);
        log.info("Customer profile created for login: {}", user.getLogin());
    }

    @Override
    public CustomerDto getCustomer(Long customerId) {
        log.info("Fetching customer with ID: {}", customerId);
        return customerRepo.findById(customerId)
                .map(Customer::build)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        log.info("Fetching all customers");
        List<CustomerDto> customers = customerRepo.findAll().stream()
                .map(Customer::build)
                .collect(Collectors.toList());
        log.debug("Total customers found: {}", customers.size());
        return customers;
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(Long customerId, CustomerDto dto) {
        log.info("Updating customer with ID: {}", customerId);
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
        if (dto.getBalance() >= 0) {
            customer.setBalance(dto.getBalance());
            log.debug("Updated balance for customer ID {}: {}", customerId, dto.getBalance());
        }
        customerRepo.save(customer);
        log.info("Customer updated successfully: {}", customerId);
        return customer.build();
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
        customerRepo.delete(customer);
        log.info("Customer deleted: {}", customerId);
    }

    @Override
    public Optional<CustomerDto> getCustomerByLogin(String login) {
        log.info("Fetching customer by login: {}", login);
        return customerRepo.findByUserAccountLogin(login)
                .map(Customer::build);
    }

    @Override
    @Transactional
    public CustomerDto topUpBalance(Long customerId, double amount) {
        log.info("Topping up balance for customer ID {} with amount: {}", customerId, amount);
        if (amount <= 0) {
            log.error("Amount must be positive: {}", amount);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                });
        double newBalance = customer.getBalance() + amount;
        customer.setBalance(newBalance);
        customerRepo.save(customer);
        log.info("Balance topped up for customer ID {}: new balance {}", customerId, newBalance);
        return customer.build();
    }
}