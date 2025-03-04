package farming.products.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import farming.products.entity.Product;

public interface ProductsRepository extends JpaRepository<Product, Long>{

	List<Product> findByPriceBetween(double minPrice, double maxPrice);

}
