package six.liang.model;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AmountDiscountRepository extends JpaRepository<AmountDiscount, Integer> { 
	@Query("SELECT ad.id FROM AmountDiscount ad WHERE :total >= ad.minPurchaseAmount "
            + "AND :currentDate BETWEEN ad.startDate AND ad.endDate")
    Integer findAmountDiscountId(@Param("total") Integer total, @Param("currentDate") Date orderDate);

}
