package six.queenie.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import six.hsiao.model.MembersBean;



public interface OrdersRepository extends JpaRepository<Orders, Integer> {
	Orders findByOrderId(Integer orderId);
	List<Orders> findByMembers(MembersBean members);

	

}
