package farming.products.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import farming.products.entity.RemoveProductData;

public interface RemoveProductDataRepository extends JpaRepository<RemoveProductData, Long>{

	@Query("SELECT rpd FROM RemoveProductData rpd JOIN rpd.product p JOIN p.farmers f WHERE f.id = :farmerId")
    List<RemoveProductData> findByFarmerId(@Param("farmerId") Long farmerId);
}


