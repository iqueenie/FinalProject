package six.queenie.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import six.queenie.model.OrderDetailRepository;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;

@Service
@Transactional
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository oDRepository;

    public List<OrderDetails> getOrderDetailsByOrderId(Integer orderId) {
        return oDRepository.findByOrderId(orderId);
    }
    
    public void insertOrderDetails(List<OrderDetails> orderDetailsList, Orders order) {
        
        oDRepository.saveOrderDetails(orderDetailsList, order); 
    }
    
    public List<Object[]> getMonthlySalesByProductType() {
        LocalDate startDate = LocalDate.now().minusMonths(12).withDayOfMonth(1);
        return oDRepository.findMonthlySalesByProductType(startDate);
    }
    
    public List<Object[]> getTotalSalesByProductType() {
        LocalDate startDate = LocalDate.now().minusMonths(12).withDayOfMonth(1);
        return oDRepository.findTotalSalesByProductType(startDate);
    }

    public List<Object[]> getTop5BestSellingProducts() {
        return oDRepository.findTop5BestSellingProducts();
    }
}
