package six.queenie.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
	Orders findByOrderId(Integer orderId);

}
