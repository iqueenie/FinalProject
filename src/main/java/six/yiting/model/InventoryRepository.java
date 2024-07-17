package six.yiting.model;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import six.yiting.model.StoresBean;


public interface InventoryRepository extends JpaRepository<InventoryBean, Integer>  {
	InventoryBean findByProductIdAndDeliveryDateAndStore(int productId,LocalDate deliveryDate,StoresBean store);
	InventoryBean findByStoreAndProductId(StoresBean store, int productId);
}
