package six.pinhong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import six.pinhong.model.Product;
import six.pinhong.service.ProductService;

@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/GetAllProudcts")
	public String getAllProudcts(Model m) {

		List<Product> products = productService.findAll();

		m.addAttribute("products", products);
	
	return "back/pinhong/GetAllProduct";
	}
}
