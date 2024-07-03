package six.queenie.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.service.OrderDetailService;
import six.queenie.service.OrderService;
import six.queenie.service.StoresService;
import six.yiting.model.StoresBean;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	@Autowired
	private ProductService pService;
	@Autowired
	private StoresService stService;
	
	@Autowired
	private OrderDetailService odService;
	
	@GetMapping("/GetAllOrders")
	public String getAllOrders(Model model) {
		
		List<Orders> orders = orderService.findAll();
		model.addAttribute("orders", orders);
		
		return "back/queenie/GetAllOrders";
	}
	
	@PostMapping("/deleteOrder")
    public String deleteOrder(@RequestParam("orderId") Integer orderId, Model model) {
        boolean updated = orderService.updateOrderStatus(orderId, "Canceled");
        if (updated) {
            model.addAttribute("message", "訂單刪除成功！");
        } else {
            model.addAttribute("message", "未找到訂單或訂單刪除失敗。");
        }
        return "back/queenie/GetAllOrders";
    }
	@GetMapping("/getStoreProduct")
    public String showInsertOrder(Model model){
        List<StoresBean> storeList = stService.findAll();
        List<Product> productList = pService.findAll();
        model.addAttribute("storeList", storeList);
        model.addAttribute("productList", productList);
        
        return "back/queenie/InsertOrder"; 
    }

    @PostMapping("/insertOrder")
    public String insertOrder(Model model, HttpServletRequest request) {
        Orders order = orderService.insertOrder(request);
        model.addAttribute("order", order);
        return "redirect:/GetAllOrders"; 
    }
    @GetMapping("/showOrder")
    public String showUpdate(@RequestParam("orderId") Integer orderId,Model model) {
    	
    	Orders order = orderService.getOrderById(orderId);
    	List<OrderDetails> orderDetailsList = odService.getOrderDetailsByOrderId(orderId);
    	 List<StoresBean> storeList = stService.findAll();
         List<Product> productList = pService.findAll();
        model.addAttribute("storeList", storeList);
        model.addAttribute("productList", productList);

		model.addAttribute("order", order);
		model.addAttribute("orderDetailsList", orderDetailsList);
        return "back/queenie/UpdateOrder"; 
    }
   
	
	@PostMapping("/updateOrder")
	public String updateOrder(@RequestParam("orderId") Integer orderId,
							  @RequestParam("storeId") Integer storeId,
							  @RequestParam("paymentMethod") String paymentMethod,
							  @RequestParam("orderDate") Date orderDate,
							  @RequestParam("status") String status) {
		Orders updateBean = orderService.getOrderById(orderId);			
		StoresBean storesBean = new StoresBean();
		storesBean.setStoreId(storeId);
		updateBean.setStoresBean(storesBean);
		 
		updateBean.setPaymentMethod(paymentMethod);
		updateBean.setOrderDate(orderDate);
		updateBean.setStatus(status);

		orderService.updateOrder(updateBean);
		return "redirect:/GetAllOrders";
	}


}
