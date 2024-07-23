package six.pinhong.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.pinhong.model.Product;
import six.pinhong.model.ProductFavorite;
import six.pinhong.service.MemberActionLogService;
import six.pinhong.service.ProductFavoriteService;
import six.pinhong.service.ProductService;

@Controller
public class ProductFavoriteController {
	
    private static final Logger logger = LoggerFactory.getLogger(ProductFavoriteController.class);


	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductFavoriteService productFavoriteService;
	
	@Autowired
    private MemberActionLogService memberActionLogService;

	
	// 新增評論
	@PostMapping("/public/InsertProductFavorite")
    public String productReviewInsert(
            @RequestParam Integer productId,
            @RequestParam Integer memberId,
            RedirectAttributes redirectAttributes,
            Model m
            ) throws IOException {

        String error = "您的收藏已達上限，請點擊鏈結刪除後再嘗試";
        String success = "收藏商品成功!";
        String alreadyHaveData = "收藏失敗，您已收藏過此商品!";

        if (productFavoriteService.getFavoritesByMemberId(memberId).size() >= 5 &&
                productFavoriteService.getFavoritesByMemberIdAndProductProductId(memberId, productId) == null) {
        	// 會員點收藏時，如果滿了，會留下這筆紀錄及時間點        	
            memberActionLogService.logAction(memberId, "點擊收藏，但已達到上限5次", productId);
            redirectAttributes.addFlashAttribute("errors", error);
            return "redirect:/public/Products/" + productId;
        }

        if (productFavoriteService.getFavoritesByMemberIdAndProductProductId(memberId, productId) == null) {
            productFavoriteService.insertProductFavorite(productId, memberId);
            
            memberActionLogService.logAction(memberId, "收藏成功", productId);
            redirectAttributes.addFlashAttribute("success", success);
            return "redirect:/public/Products/" + productId;
        }
        
        memberActionLogService.logAction(memberId, "點擊收藏，但已收藏過此商品", productId);
        redirectAttributes.addFlashAttribute("alreadyHaveData", alreadyHaveData);
        return "redirect:/public/Products/" + productId;
    }
	
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
	