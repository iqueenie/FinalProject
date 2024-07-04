package six.queenie.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;

public interface OrderDetailRepository extends JpaRepository<OrderDetails, Integer> {

	List<OrderDetails> findByOrderId(Integer orderId);

	@Transactional
    default void saveOrderDetails(List<OrderDetails> orderDetailsList, Orders order) {
        for (OrderDetails orderDetails : orderDetailsList) {
            orderDetails.setOrders(order);
        }
        saveAll(orderDetailsList);
    }

}
