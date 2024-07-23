package six.pinhong.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> { 

	// shop.html - 前台頁碼、查詢
	@Query("SELECT p FROM Product p WHERE " +
			"p.productPublished = 1 AND " +
		    "(:searchTerm IS NULL OR :searchTerm = '' OR " +
		    "LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
		    "LOWER(p.productDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
		    "AND (:productType IS NULL OR :productType = '' OR p.productType = :productType)")
	Page<Product> searchProducts(String searchTerm, String productType, Pageable pageable);
	
	// 有上架的商品
	@Query("SELECT p FROM Product p WHERE p.productPublished = 1")
	Page<Product> findAllPublished(Pageable pageable);
	
	// 隨機五個同類型商品當推薦，且排除目前正在單一商品頁查看的productId
    @Query(value = "SELECT TOP 5 * FROM Product p WHERE p.productPublished = 1 " +
    		 "AND p.productType = :productType " +
    		 "AND p.productId <> :currentProductId ORDER BY NEWID()", nativeQuery = true)
    List<Product> findSametype5Products(String productType, Integer currentProductId);
	
	List<Product> findTop5ByOrderByProductQuantityDesc();
	
	List<Product> findTop10ByOrderByProductIdDesc();
	Page<Product> findByProductNameContainingAndProductTypeContaining(String productName, String productType, Pageable pageable);
	Page<Product> findByProductNameContaining(String productName, Pageable pageable);
	Page<Product> findByProductTypeContaining(String productType, Pageable pageable);
	Page<Product> findByProductPublished(int published, Pageable pageable);
}
