package farming.customer.controllers;

import farming.accounting.entity.UserAccount;
import farming.customer.dto.CustomerDto;
import farming.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerDto> getCurrentCustomer(@AuthenticationPrincipal UserAccount user) {
        log.info("Fetching current customer for user: {}", user != null ? user.getLogin() : "null");
        if (user == null) {
            log.error("User not authenticated");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
         CustomerDto customer = customerService.getCustomerByLogin(user.getLogin())
                .orElseThrow(() -> {
                    log.error("Customer profile not found for user: {}", user.getLogin());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer profile not found for user: " + user.getLogin());
                });
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable Long customerId) {
        log.info("Fetching customer with ID: {}", customerId);
        CustomerDto customer = customerService.getCustomer(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        log.info("Fetching all customers");
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDto dto) {
        log.info("Updating customer with ID: {}", customerId);
        CustomerDto updatedCustomer = customerService.updateCustomer(customerId, dto);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{customerId}/top-up")
    public ResponseEntity<CustomerDto> topUpBalance(@PathVariable Long customerId, @RequestParam double amount) {
        log.info("Request to top up balance for customer ID: {} with amount: {}", customerId, amount);
        CustomerDto updatedCustomer = customerService.topUpBalance(customerId, amount);
        return ResponseEntity.ok(updatedCustomer);
    }
}