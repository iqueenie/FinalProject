package six.liang.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscountProductRepository extends JpaRepository<ProductDiscount, Integer> {
    List<ProductDiscount> findByProductProductId(Integer productId);
}
