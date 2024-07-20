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
			RedirectAttributes redirectAttributes // 使用 RedirectAttributes
			) throws IOException{
		
		String error="最多收藏三個追蹤商品，請刪除後再嘗試";
		
		if (productFavoriteService.getFavoritesByMemberId(memberId).size() >= 3 &&
				productFavoriteService.getFavoritesByMemberIdAndProductProductId(memberId, productId) == null) {
			
	        redirectAttributes.addFlashAttribute("errors", error); // 使用 addFlashAttribute
			return "redirect:/public/Products/"+ productId;
		}
		
		if (productFavoriteService.getFavoritesByMemberIdAndProductProductId(memberId, productId) == null) {
			productFavoriteService.insertProductFavorite(productId, memberId);
			return "front/pinhong/ProductFavorite";
		}

	    return "front/pinhong/ProductFavorite";
	};
	
}
	