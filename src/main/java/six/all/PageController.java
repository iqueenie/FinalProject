package six.all;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import six.liang.model.AmountDiscount;
import six.liang.model.HolidayDiscount;
import six.liang.model.ProductDiscount;
import six.liang.service.AmountDiscountService;
import six.liang.service.HolidayDiscountService;
import six.liang.service.ProductDiscountService;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;



@Controller
public class PageController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private HolidayDiscountService holidayDiscountService;

	@Autowired
	private AmountDiscountService amountDiscountService;
	
	@Autowired
	private ProductDiscountService productDiscountService;
	
	@GetMapping("/private/back")
	public String backIndex() {
		return "/back/Index";
	}
	
	@GetMapping("/public/front")
	public String frontIndex(Model model) {
	    Map<String, Object> homePageDetails = productService.getHomePageDetails();
	    
	    List<Product> products = productService.findTop5ByOrderByProductQuantityDesc();

	    Map<Integer, ProductDiscount> productDiscountMap = new HashMap<>();
	    Map<Integer, Integer> roundedDiscountedPriceMap = new HashMap<>();
	    
	    for (Product product : products) {
	        List<ProductDiscount> productDiscounts = productDiscountService.findByProductId(product.getProductId());
	        if (!productDiscounts.isEmpty() && productDiscounts.get(0).getIsActive() == 1) { // 確認折扣是否上架
	            ProductDiscount discount = productDiscounts.get(0);
	            productDiscountMap.put(product.getProductId(), discount);
	            double discountedPrice = product.getProductPrice() * (1 - discount.getDiscountPercentage() / 100.0);
	            roundedDiscountedPriceMap.put(product.getProductId(), (int) Math.round(discountedPrice));
	        } else {
	            productDiscountMap.put(product.getProductId(), null); // 如果没有折扣，映射到null
	        }
	    }

	    // 獲取當前優惠
	    List<HolidayDiscount> holidayDiscounts = holidayDiscountService.findCurrentDiscounts();
	    List<AmountDiscount> amountDiscounts = amountDiscountService.findCurrentDiscounts();

	    // 轉換圖片編碼
	    List<String> holidayDiscountImagesBase64 = holidayDiscounts.stream()
	            .map(discount -> holidayDiscountService.getDiscountImageBase64(discount.getDiscountImage()))
	            .collect(Collectors.toList());
	    
	    List<String> amountDiscountImagesBase64 = amountDiscounts.stream()
	            .map(discount -> amountDiscountService.getDiscountImageBase64(discount.getDiscountImage()))
	            .collect(Collectors.toList());
	    

	    model.addAttribute("holidayDiscounts", holidayDiscounts);
	    model.addAttribute("amountDiscounts", amountDiscounts);
	    model.addAttribute("holidayDiscountImagesBase64", holidayDiscountImagesBase64);
	    model.addAttribute("amountDiscountImagesBase64", amountDiscountImagesBase64);
	    model.addAttribute("productDiscountMap", productDiscountMap);
	    model.addAttribute("roundedDiscountedPriceMap", roundedDiscountedPriceMap);
	    
	    
	    model.addAttribute("products", homePageDetails.get("products"));
	    model.addAttribute("new10Products", homePageDetails.get("new10Products"));
	    model.addAttribute("averageRatings", homePageDetails.get("averageRatings"));
	    model.addAttribute("reviewCounts", homePageDetails.get("reviewCounts"));

	    return "/front/index";
	}
}
