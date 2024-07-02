package six.queenie.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.queenie.model.OrderDetailRepository;
import six.queenie.model.OrderDetails;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public List<OrderDetails> getOrderDetailsByOrderId(int orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
