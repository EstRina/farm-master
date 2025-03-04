package farming.customer.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import farming.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

	Optional<Customer> findByUserAccountLogin(String login);
    boolean existsByUserAccountLogin(String login);
}
