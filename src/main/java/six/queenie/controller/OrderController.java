package six.queenie.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import six.queenie.model.Orders;
import six.queenie.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping("/GetAllOrders")
	public String getAllOrders(Model model) {
		
		List<Orders> orders = orderService.findAll();
		model.addAttribute("orders", orders);
		
		return "end/queenie/GetAllOrders";
	}
	
	@PostMapping("/deleteOrder")
    public String deleteOrder(@RequestParam("orderId") int orderId, Model model) {
        boolean updated = orderService.updateOrderStatus(orderId, "Canceled");
        if (updated) {
            model.addAttribute("message", "訂單刪除成功！");
        } else {
            model.addAttribute("message", "未找到訂單或訂單刪除失敗。");
        }
        return "end/queenie/GetAllOrders";
    }

}
