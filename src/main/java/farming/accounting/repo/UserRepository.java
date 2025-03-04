package farming.accounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import farming.accounting.entity.UserAccount;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, String>{

}
