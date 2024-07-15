package six.pinhong.model;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> { 

	List<ProductReview> findByProductProductId(Integer productId);
	
	  @Query(value = "SELECT TOP 2 * FROM ProductReview WHERE productId = :productId ORDER BY reviewTime DESC, stars DESC", nativeQuery = true)
	  List<ProductReview> findTop4ByProductIdOrderByReviewTimeDesc(Integer productId);
}

