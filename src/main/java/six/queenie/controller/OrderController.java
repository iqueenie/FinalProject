package six.queenie.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import six.hsiao.model.MembersBean;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.service.OrderDetailService;
import six.queenie.service.OrderService;
import six.yiting.model.StoresBean;
import six.yiting.service.StoreService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	@Autowired
	private ProductService pService;
	@Autowired
	private StoreService stService;
	
	@Autowired
	private OrderDetailService odService;
	

	
	@GetMapping("/private/GetAllOrders")
	public String getAllOrders(Model model) {
		
		List<Orders> orders = orderService.findAll();
		model.addAttribute("orders", orders);
		
		return "back/queenie/GetAllOrders";
	}
	
	@PostMapping("/deleteOrder")
    public String deleteOrder(@RequestParam("orderId") Integer orderId, Model model) {
        orderService.updateOrderStatusAndPoints(orderId, "Canceled");
        model.addAttribute("orders", orderService.findAll()); 
        return "back/queenie/GetAllOrders";
    }
	
	
	@GetMapping("/private/getStoreProduct")
    public String showInsertOrder(Model model){
        List<StoresBean> storeList = stService.findAllStores();
        List<Product> productList = pService.findAll();      
        model.addAttribute("storeList", storeList);
        model.addAttribute("productList", productList);
        
        return "back/queenie/InsertOrder"; 
    }

	@PostMapping("/private/insertOrder")
    public String insertOrder(Model model, HttpServletRequest request) {
        Orders order = orderService.insertOrder(request);
        model.addAttribute("order", order);
        return "redirect:/private/GetAllOrders"; 
    }

    @GetMapping("/private/showOrder")
    public String showUpdate(@RequestParam("orderId") Integer orderId,Model model) {
    	
    	Orders order = orderService.getOrderById(orderId);
    	List<OrderDetails> orderDetailsList = odService.getOrderDetailsByOrderId(orderId);
    	 List<StoresBean> storeList = stService.findAllStores();
         List<Product> productList = pService.findAll();
        model.addAttribute("storeList", storeList);
        model.addAttribute("productList", productList);

		model.addAttribute("order", order);
		model.addAttribute("orderDetailsList", orderDetailsList);
        return "back/queenie/UpdateOrder"; 
    }
   
	
	@PostMapping("/private/updateOrder")
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

		orderService.updateOrder(orderId, orderDate);
		return "redirect:/private/GetAllOrders";
	}


}
