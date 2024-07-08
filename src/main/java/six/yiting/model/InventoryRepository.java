package six.yiting.model;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryBean, Integer>  {
	InventoryBean findByProductIdAndDeliveryDateAndStore(int productId,LocalDate deliveryDate,StoresBean store);
}
