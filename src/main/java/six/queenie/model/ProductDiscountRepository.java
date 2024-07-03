package six.queenie.model;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import six.liang.model.ProductDiscount;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {
	
	@Query("SELECT pd.id FROM ProductDiscount pd WHERE pd.productId = :productId "
            + "AND :currentDate BETWEEN pd.startDate AND pd.endDate")
    Integer findProductDiscountId(@Param("productId") Integer productId, @Param("currentDate") Date orderDate);

}
