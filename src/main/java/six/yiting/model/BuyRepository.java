package six.yiting.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;


public interface BuyRepository extends JpaRepository<BuyBean, Integer>  {
	
	BuyBean findByArrivedDateAndStore(LocalDate arrivedDate,StoresBean store);
	List<BuyBean> findByStore(StoresBean store);
	List<BuyBean> findByArrivedDateAndCheckToInv(LocalDate arrivedDate,Boolean checkInv);
}
