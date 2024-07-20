package six.pinhong.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Error;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.pinhong.model.Product;
import six.pinhong.model.ProductFavorite;
import six.pinhong.model.ProductImage;
import six.pinhong.model.ProductReview;
import six.pinhong.service.ProductFavoriteService;
import six.pinhong.service.ProductReviewService;
import six.pinhong.service.ProductService;
import six.queenie.model.OrderDetailRepository;
import six.queenie.model.OrderDetails;

@Controller
public class ProductFavoriteController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductFavoriteService productFavoriteService;
	
	// 新增評論
	@PostMapping("/public/InsertProductFavorite")
	public String productReviewInsert(
			@RequestParam Integer productId,
			@RequestParam Integer memberId,
			RedirectAttributes redirectAttributes, // 使用 RedirectAttributes
			Model m
			) throws IOException{
		
		String error="您已收藏5個商品，請刪除後再嘗試";
		String success="恭喜您，收藏商品成功!";
		String alreadyHaveData="很抱歉，您已收藏過此商品!";
		
		if (productFavoriteService.getFavoritesByMemberId(memberId).size() >= 5 &&
				productFavoriteService.getFavoritesByMemberIdAndProductProductId(memberId, productId) == null) {
			
	        redirectAttributes.addFlashAttribute("errors", error); // 使用 addFlashAttribute
			return "redirect:/public/Products/"+ productId;
		}
		
		if (productFavoriteService.getFavoritesByMemberIdAndProductProductId(memberId, productId) == null) {
			productFavoriteService.insertProductFavorite(productId, memberId);
			redirectAttributes.addFlashAttribute("success", success); // 使用 addFlashAttribute
			return "redirect:/public/Products/"+ productId;
		}
		
		redirectAttributes.addFlashAttribute("alreadyHaveData", alreadyHaveData); // 使用 addFlashAttribute
	    return "redirect:/public/Products/"+ productId;
	};
	
	// 查看總收藏
	@GetMapping("/public/allFavoriteProducts")
	public String showRecentProducts(HttpSession session, Model model) {
	    Integer memberId = null;
	    if (session.getAttribute("loggedInMember") != null) {
	        memberId = ((MembersBean) session.getAttribute("loggedInMember")).getMemberId();
	    } else {
			return "redirect:/public/frontLoginMain";
		}
	    
		List<ProductFavorite> favoriteProducts = productFavoriteService.getFavoritesByMemberId(memberId);
	    List<Product> products = new ArrayList<>();
	    for (ProductFavorite favorite : favoriteProducts) {
	        Product product = productService.findProductById(favorite.getProduct().getProductId());
	        products.add(product);
	    }
	    // 所有商品的平均評分和評論總數
	    Map<Integer, Double> averageRatings = productService.getAverageRatings();
	    Map<Integer, Integer> reviewCounts = productService.getReviewCounts();

	    // 為每個最近查看的商品 add 平均評分和評論總數
	    List<Map<String, Object>> allFavoriteProducts = new ArrayList<>();
	    for (Product product : products) {
	        Map<String, Object> productData = new HashMap<>();
	        productData.put("product", product);
	        productData.put("averageRating", averageRatings.getOrDefault(product.getProductId(), 0.0));
	        productData.put("reviewCount", reviewCounts.getOrDefault(product.getProductId(), 0));
	        allFavoriteProducts.add(productData);
	    }
	    
	    if (allFavoriteProducts.isEmpty()) {
	        model.addAttribute("message", "您目前沒有任何的收藏商品，點擊按鈕可跳至商品頁");
	    } else {
	        model.addAttribute("allFavoriteProducts", allFavoriteProducts);
	    }
	    
	    return "front/pinhong/ProductFavorite";
	}
	
	//刪除該收藏商品
	@DeleteMapping("/public/allFavoriteProducts/delete")
	public String productDelete(@RequestParam("productId") Integer productId) {
		productFavoriteService.deleteProductFavoriteByProductId(productId);
		
		 return "redirect:/public/allFavoriteProducts";
	}
}
	