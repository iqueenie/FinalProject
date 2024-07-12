package six.pinhong.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> { 

	// shop.html - 前台頁碼、查詢
	@Query("SELECT p FROM Product p WHERE " +
		       "(:searchTerm IS NULL OR :searchTerm = '' OR " +
		       "LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		       "LOWER(p.productDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
		       "AND (:productType IS NULL OR :productType = '' OR p.productType = :productType)")
	Page<Product> searchProducts(String searchTerm, String productType, Pageable pageable);
	 
	List<Product> findTop5ByOrderByProductQuantityDesc();
	
	List<Product> findTop10ByOrderByProductIdDesc();
	}

