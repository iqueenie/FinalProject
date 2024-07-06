package six.liang.model;

import six.pinhong.model.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountProductRepository extends JpaRepository<ProductDiscount, Integer> {
    
	List<Product> findByProductId(Integer productId);

	boolean existsByProductId(Integer productId);
	
}
