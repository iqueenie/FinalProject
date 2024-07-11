package six.all;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import six.pinhong.model.Product;
import six.pinhong.service.ProductService;



@Controller
public class PageController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/private/back")
	public String backIndex() {
		return "/back/Index";
	}
	
	@GetMapping("/public/front")
	public String frontIndex(Model model) {
		List<Product> products = productService.findTop5ByOrderByProductQuantityDesc();
		List<Product> new5Products = productService.findTop5ByOrderByProductIdDesc();
		
		model.addAttribute("products", products);
		model.addAttribute("new5Products", new5Products);
		return "/front/index";
	}
}
