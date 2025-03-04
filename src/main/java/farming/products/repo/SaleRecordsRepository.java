package farming.products.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import farming.products.entity.Product;
import farming.products.entity.SaleRecords;

public interface SaleRecordsRepository extends JpaRepository<SaleRecords, Long>{

	List<SaleRecords> findByProduct(Product product);
    List<SaleRecords> findByCustomerId(Long customerId);
    
//    @Query("SELECT sr FROM SaleRecords sr WHERE sr.farmer.id = :farmerId")
//    List<SaleRecords> findByFarmerId(@Param("farmerId") Long farmerId);
	
    List<SaleRecords> findByFarmerFarmerId(Long farmerId);
}
