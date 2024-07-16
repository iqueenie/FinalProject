package six.all;

import java.util.List;
import java.util.Map;

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
	    Map<String, Object> homePageDetails = productService.getHomePageDetails();
	    
	    System.out.println(homePageDetails.get("averageRatings"));
	    System.out.println(homePageDetails.get("products"));
	    

	    model.addAttribute("products", homePageDetails.get("products"));
	    model.addAttribute("new10Products", homePageDetails.get("new10Products"));
	    model.addAttribute("averageRatings", homePageDetails.get("averageRatings"));
	    model.addAttribute("reviewCounts", homePageDetails.get("reviewCounts"));

	    return "/front/index";
	}
}
