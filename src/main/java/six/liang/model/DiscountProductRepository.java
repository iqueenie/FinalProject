package six.liang.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface DiscountProductRepository extends JpaRepository<ProductDiscount, Integer> {
    List<ProductDiscount> findByProductId(Integer productId);
    
    @Query("SELECT pd.id FROM ProductDiscount pd WHERE pd.productId = :productId "
            + "AND :currentDate BETWEEN pd.startDate AND pd.endDate")
    Integer findProductDiscountId(@Param("productId") Integer productId, @Param("currentDate") Date orderDate);
}
