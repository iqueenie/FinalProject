package six.queenie.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetails, Integer> {

	List<OrderDetails> findByOrderId(int orderId);
}
