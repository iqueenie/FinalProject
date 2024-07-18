package six.liang.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AmountDiscountRepository extends JpaRepository<AmountDiscount, Integer> {
	@Query("SELECT ad.id FROM AmountDiscount ad WHERE :total >= ad.minPurchaseAmount "
            + "AND :currentDate BETWEEN ad.startDate AND ad.endDate")
    Integer findAmountDiscountId(@Param("total") Integer total, @Param("currentDate") Date orderDate);

    List<AmountDiscount> findByIsActiveTrue();
    List<AmountDiscount> findByStartDateBeforeAndEndDateAfterAndIsActive(LocalDate currentDateStart, LocalDate currentDateEnd, Integer isActive);
    List<AmountDiscount> findByDiscountDescriptionContainingAndIsActiveTrue(String description);
    
    Optional<AmountDiscount> findByDiscountName(String discountName);
}

