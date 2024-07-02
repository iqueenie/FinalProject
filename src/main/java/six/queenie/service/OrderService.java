package six.queenie.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.queenie.model.Orders;
import six.queenie.model.OrdersRepository;

@Service
@Transactional
public class OrderService {
	
	@Autowired
	private OrdersRepository ordersRepository;
	
	public List<Orders> findAll() {
		return ordersRepository.findAll();
		
	}
	
	 public Orders getOrderById(Integer orderId) {
	       
         return ordersRepository.findByOrderId(orderId);
     }
	 public boolean updateOrderStatus(Integer orderId, String newStatus) {
     Orders order = ordersRepository.findByOrderId(orderId);
     if (order != null) {
         order.setStatus(newStatus);
         ordersRepository.save(order);
         return true;
     }
     return false;
 }
	 

}
