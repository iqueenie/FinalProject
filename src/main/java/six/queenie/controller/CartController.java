package six.queenie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.model.Orders;
import six.queenie.service.CartService;
import six.queenie.service.OrderService;

@Controller
public class CartController {
	@Autowired
	private OrderService oService;
	@Autowired
	private CartService cService;
	
	@Autowired
	private ProductService pService;
	
	@GetMapping("/GetAllCart")
	public String ShowCart(Model model, HttpSession session) {
		
		Orders order = (Orders) session.getAttribute("order");
		if (order == null) {
	        order = new Orders(); 
	        session.setAttribute("order", order); 
	    }
		 model.addAttribute("order", order);
		
		return "front/queenie/cart";
		
	}
	
	@GetMapping("/product/{productId}")
    public String getProductPage(@PathVariable("productId") Integer productId, Model model) {
        
        Product product = pService.findProductById(productId);
        model.addAttribute("product", product);
        return "front/pingshop"; 
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public String addToCart(@RequestParam("productId") Integer productId) {
        
    	cService.addToCart(productId);
        return "商品添加成功";
    }
    
    @PostMapping("/deleteProduct")
    public String deleteOrder(@RequestParam("productId") Integer orderId, Model model) {
       
        return "front/queenie/GetAllCart";
    }
}


