package farming.farmer.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import farming.farmer.entity.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, Long>{

	boolean existsByUserAccountLogin(String login);

	Optional<Farmer> findByUserAccountLogin(String login);

}
