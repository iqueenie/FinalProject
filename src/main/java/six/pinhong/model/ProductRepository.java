package six.pinhong.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> { 

    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:searchTerm% OR p.productDescription LIKE %:searchTerm%")
    	Page<Product> searchProducts(String searchTerm, Pageable pageable);
	 
	List<Product> findTop5ByOrderByProductQuantityDesc();
	
	List<Product> findTop5ByOrderByProductIdDesc();
	}

