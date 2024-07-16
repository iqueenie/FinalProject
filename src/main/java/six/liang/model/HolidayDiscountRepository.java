package six.liang.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayDiscountRepository extends JpaRepository<HolidayDiscount, Integer> {
    List<HolidayDiscount> findByIsActiveTrue();
    List<HolidayDiscount> findByDiscountNameContainingAndIsActiveTrue(String discountName);
    Optional<HolidayDiscount> findByDiscountName(String discountName);
}