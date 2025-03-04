package farming.customer.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import farming.accounting.entity.UserAccount;
import farming.customer.dto.CustomerDto;
import farming.farmer.dto.FarmerDto;
import farming.products.dto.ProductDto;
import farming.products.dto.RemoveProductDataDto;
import farming.products.dto.SaleRecordsDto;

public interface ICustomerService {
	
	void createCustomerProfile(UserAccount user);
    CustomerDto getCustomer(Long customerId);
    List<CustomerDto> getAllCustomers();
    CustomerDto updateCustomer(Long customerId, CustomerDto dto);
    void deleteCustomer(Long customerId);
	Optional<CustomerDto> getCustomerByLogin(String login);
	CustomerDto topUpBalance(Long customerId, double amount);
	
	
	
	
}
