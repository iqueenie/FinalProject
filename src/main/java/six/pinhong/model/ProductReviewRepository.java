package six.pinhong.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> { 

	List<ProductReview> findByProductProductId(Integer productId);
	
	  @Query(value = "SELECT TOP 2 * FROM ProductReview WHERE productId = :productId ORDER BY reviewTime DESC, stars DESC", nativeQuery = true)
	  List<ProductReview> findTop2ByProductIdOrderByReviewTimeDesc(Integer productId);
	  
	  // 查詢商品所有評論並按時間排序
	  @Query("SELECT pr FROM ProductReview pr WHERE pr.product.productId = ?1 ORDER BY pr.reviewTime DESC")
	  Page<ProductReview> findByProductProductId(Integer productId, Pageable pageable);
}

