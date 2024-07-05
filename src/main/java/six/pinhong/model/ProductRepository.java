package six.pinhong.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> { 

	@Query("SELECT p FROM Product p WHERE " +
	           "LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ")
	    List<Product> searchProducts(@Param("searchTerm") String searchTerm);
	}

