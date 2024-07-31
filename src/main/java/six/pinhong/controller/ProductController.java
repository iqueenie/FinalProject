package six.pinhong.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.liang.model.ProductDiscount;
import six.liang.service.ProductDiscountService;
import six.pinhong.model.Product;
import six.pinhong.model.ProductImage;
import six.pinhong.model.ProductReview;
import six.pinhong.service.MemberActionLogService;
import six.pinhong.service.ProductReviewService;
import six.pinhong.service.ProductService;
import six.queenie.model.OrderDetailRepository;
import six.queenie.model.OrderDetails;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductReviewService productReviewService;
	
	@Autowired
	private OrderDetailRepository orderDetailRepo;
	
	@Autowired
	private ProductDiscountService productDiscountService;
	
	@Autowired
	private MemberActionLogService memberActionLogService;
	
	// 後台查全部
	@GetMapping("/private/Product/GetAll")
	public String getAllProudcts(Model m) {

		List<Product> products = productService.findAll();
	    Map<Integer, Double> averageRatings = productService.getAverageRatings();
	    Map<Integer, Integer> reviewCounts = productService.getReviewCounts();

		m.addAttribute("products", products);
		m.addAttribute("averageRatings", averageRatings);
		m.addAttribute("reviewCounts", reviewCounts);

		return "back/pinhong/GetAllProduct";
	}
	
	// 後台查全部，Ajax的Api
	@GetMapping("/Product/findAllAjax")
	@ResponseBody
	public List<Map<String, Object>> findAllAjax() {
		
		List<Product> products = productService.findAll();
	    Map<Integer, Double> averageRatings = productService.getAverageRatings();
	    Map<Integer, Integer> reviewCounts = productService.getReviewCounts();

	    List<Map<String, Object>> response = new ArrayList<>();
	    for (Product product : products) {
	        Map<String, Object> productMap = new HashMap<>();
	        productMap.put("productId", product.getProductId());
	        productMap.put("productName", product.getProductName());
	        productMap.put("productType", product.getProductType());
	        productMap.put("productCost", product.getProductCost());
	        productMap.put("productPrice", product.getProductPrice());
	        productMap.put("productExpirydate", product.getProductExpirydate());
	        productMap.put("productQuantity", product.getProductQuantity());
	        productMap.put("productPublished", product.getProductPublished());
	        productMap.put("averageRating", averageRatings.getOrDefault(product.getProductId(), 0.0));
	        productMap.put("reviewCount", reviewCounts.getOrDefault(product.getProductId(), 0));

	        response.add(productMap);
	    }
	    return response;
	}
		
	
	// 新增進入點
	@GetMapping("/private/Product/AddProductPage")
	public String productInsertMain() {

		return "back/pinhong/InsertProduct";
	}
	
	// 後台新增商品 (傳統方法)
	@PostMapping("/private/Product/InsertProduct")
	public String productInsert(
			@RequestParam String productName,
			@RequestParam String productType,
			@RequestParam Integer productCost,
			@RequestParam Integer productPrice,
			@RequestParam Integer productExpirydate,
			@RequestParam String productDescription,
			@RequestParam Integer productPublished,
			@RequestParam Integer productQuantity,
			@RequestParam MultipartFile imageFile,
			Model m
			) throws IOException{
		
	    List<Product> products = productService.findAll();

	    for (Product orgainProduct : products) {
	        if (productName.equals(orgainProduct.getProductName())) {
	            m.addAttribute("errors", "已有相同產品，請重新輸入");
	            return "back/pinhong/InsertProduct";
	        }
	    }
	    // 這裡將在循環外創建和保存新產品
	    Product product = new Product(productName, productType, productCost, productPrice, productExpirydate, productDescription, productPublished, productQuantity);
	    ProductImage productImage = new ProductImage(imageFile.getBytes());

	    product.setProductImage(productImage);
	    productImage.setProduct(product);

	    productService.insertProduct(product);

	    return "redirect:/private/Product/GetAll";
	};
		

	
	// ---------------------------------- 新增進入點
	@GetMapping("/InsertAllDefaultProduct")
	public String productInsertMain1() {

		return "back/pinhong/InsertAllDefaultProduct";
	}
	// 解決SQL會自動補0的問題，一次新增10張
	@PostMapping("/private/Product/InsertProductForFinalProject")
	public String productInsert(
	        @RequestParam List<String> productName,
	        @RequestParam List<String> productType,
	        @RequestParam List<Integer> productCost,
	        @RequestParam List<Integer> productPrice,
	        @RequestParam List<Integer> productExpirydate,
	        @RequestParam List<String> productDescription,
	        @RequestParam List<Integer> productPublished,
	        @RequestParam List<Integer> productQuantity,
	        @RequestParam List<MultipartFile> imageFiles,
	        Model m
	) throws IOException {
	    for (int i = 0; i < productName.size(); i++) {
	        Product product = new Product(
	            productName.get(i),
	            productType.get(i),
	            productCost.get(i),
	            productPrice.get(i),
	            productExpirydate.get(i),
	            productDescription.get(i),
	            productPublished.get(i),
	            productQuantity.get(i)
	        );
	        ProductImage productImage = new ProductImage(imageFiles.get(i).getBytes());      
	        product.setProductImage(productImage);
	        productImage.setProduct(product);
	        productService.insertProduct(product);
	    }
	    return "redirect:/private/Product/GetAll";
	}
	// ---------------------------------------------------------------------------
	
	
	
	// 刪除，Ajax
	@DeleteMapping("/private/Product/delete")
	@ResponseBody
	public String productDelete(@RequestParam("productId") Integer productId) {
		productService.deleteById(productId);
		return "success";
	}
	
	// 顯示圖片的Api
	@GetMapping("/ProductPhoto")
	public ResponseEntity<byte[]> downloadPhotos(@RequestParam Integer productId) {
		
		ProductImage productImage = productService.findImageById(productId);
		
		byte[] imageUrl = productImage.getImageUrl();
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.IMAGE_JPEG);
		
		return new ResponseEntity<byte[]>(imageUrl, headers, HttpStatus.OK);	
	}
	
	// 查詢
	@GetMapping("/private/Product/UpdatePage")
	public String productUpdateMain(@RequestParam("productId") Integer productId, Model m) {
		Product product = productService.findProductById(productId);
		ProductImage productImage = productService.findImageById(productId);
		m.addAttribute("product", product);
		m.addAttribute("productImage", productImage);
		return "back/pinhong/FindProduct";
	}

	// 更新 (MVC使用@ModelAttribute自動映射)
	@PutMapping("/private/Product/Update")
	public String productFindById(@ModelAttribute("product") Product product,
			@RequestParam MultipartFile imageFile,
			Model m) throws IOException {

	    List<Product> products = productService.findAll();

	    for (Product orgainProduct : products) {
	        if (!product.getProductId().equals(orgainProduct.getProductId()) &&
	        	// 如果這次修改產品的ID == 原本集合的ID，名字重複則不列入姓名重複的範圍 ↑
	        	product.getProductName().equals(orgainProduct.getProductName())) {
	            m.addAttribute("errors", "已有相同產品，請重新輸入");
	            return "back/pinhong/FindProduct";
	        }
	    }
								

			if (imageFile != null && !imageFile.isEmpty()) {
				ProductImage productImage = new ProductImage();
				productImage.setProductId(product.getProductId());
				productImage.setImageUrl(imageFile.getBytes());
				// 有更新圖片時new一個productImage把ID、圖片set進去
				
				productImage.setProduct(product);
				product.setProductImage(productImage); 
				// 將圖片關聯到產品
					
				productService.insertProduct(product);
			}else {
				productService.insertProduct(product); 
			}
				
		return "redirect:/private/Product/GetAll";
	}
	
	// shop.html - 前台頁碼、查詢
	@GetMapping("/public/Products")
	public String showAllProducts(@RequestParam(value = "searchTerm", defaultValue = "") String searchTerm,
	                              @RequestParam(value = "productType", defaultValue = "") String productType,
	                              @RequestParam(value = "p", defaultValue = "1") Integer pageNum,
	                              @RequestParam(value = "hasDiscount", defaultValue = "false") Boolean hasDiscount,
	                              @RequestParam(value = "fromActivity", defaultValue = "false") Boolean fromActivity,
	                              Model model,
	                              @RequestParam(value = "sortField", required = false) String sortField) {
	    int pageSize = 10;
	    Page<Product> page = productService.findByPage(searchTerm, productType, pageNum, pageSize, sortField);

	    Map<Integer, ProductDiscount> productDiscountMap = new HashMap<>();
	    Map<Integer, Integer> roundedDiscountedPriceMap = new HashMap<>();
	    Map<Integer, Double> averageRatings = productService.getAverageRatings();
	    Map<Integer, Integer> reviewCounts = productService.getReviewCounts();

	    List<Product> filteredProducts = new ArrayList<>();

	    for (Product product : page.getContent()) {
	        List<ProductDiscount> productDiscounts = productDiscountService.findByProductId(product.getProductId());
	        if (!productDiscounts.isEmpty() && productDiscounts.get(0).getIsActive() == 1) { // 確認折扣是否上架
	            ProductDiscount discount = productDiscounts.get(0);
	            productDiscountMap.put(product.getProductId(), discount);
	            double discountedPrice = product.getProductPrice() * (1 - discount.getDiscountPercentage() / 100.0);
	            roundedDiscountedPriceMap.put(product.getProductId(), (int) Math.round(discountedPrice));
	            if (hasDiscount || fromActivity) {
	                filteredProducts.add(product);
	            }
	        } else {
	            productDiscountMap.put(product.getProductId(), null);
	            if (!hasDiscount || !fromActivity) {
	                filteredProducts.add(product);
	            }
	        }
	    }

	    if (hasDiscount || fromActivity) {
	        page = new PageImpl<>(filteredProducts, PageRequest.of(pageNum - 1, pageSize), filteredProducts.size());
	    }

	    model.addAttribute("page", page);// 處理頁數
	    model.addAttribute("searchTerm", searchTerm);// 查啥產品
	    model.addAttribute("productType", productType); // 商品種類
	    model.addAttribute("productDiscountMap", productDiscountMap);
	    model.addAttribute("roundedDiscountedPriceMap", roundedDiscountedPriceMap);
	    model.addAttribute("averageRatings", averageRatings);
	    model.addAttribute("reviewCounts", reviewCounts);

	    int start = pageNum * pageSize - (pageSize - 1);
	    int end = Math.min(start + page.getNumberOfElements() - 1, (int) page.getTotalElements());

	    model.addAttribute("currentRange", start + "–" + end);

	    return "front/pinhong/shop";
	}



	// shop.html 的 Ajax Api
	@GetMapping("/public/api/products")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> showAllProductsAjax(
	        @RequestParam(value = "searchTerm", defaultValue = "") String searchTerm,
	        @RequestParam(value = "productType", defaultValue = "") String productType,
	        @RequestParam(value = "p", defaultValue = "1") Integer pageNum,
	        @RequestParam(value = "hasDiscount", defaultValue = "false") Boolean hasDiscount,
	        @RequestParam(value = "sortField", required = false) String sortField) {
	    int pageSize = 10;
	    Page<Product> page = productService.findByPage(searchTerm, productType, pageNum, pageSize, sortField);

	    Map<Integer, ProductDiscount> productDiscountMap = new HashMap<>();
	    Map<Integer, Integer> roundedDiscountedPriceMap = new HashMap<>();
	    Map<Integer, Double> averageRatings = productService.getAverageRatings();
	    Map<Integer, Integer> reviewCounts = productService.getReviewCounts();

	    List<Product> filteredProducts = new ArrayList<>();

	    for (Product product : page.getContent()) {
	        List<ProductDiscount> productDiscounts = productDiscountService.findByProductId(product.getProductId());
	        if (!productDiscounts.isEmpty() && productDiscounts.get(0).getIsActive() == 1) { // 確認折扣是否上架
	            ProductDiscount discount = productDiscounts.get(0);
	            productDiscountMap.put(product.getProductId(), discount);
	            double discountedPrice = product.getProductPrice() * (1 - discount.getDiscountPercentage() / 100.0);
	            roundedDiscountedPriceMap.put(product.getProductId(), (int) Math.round(discountedPrice));
	            if (hasDiscount) {
	                filteredProducts.add(product);
	            }
	        } else {
	            productDiscountMap.put(product.getProductId(), null);
	            if (!hasDiscount) {
	                filteredProducts.add(product);
	            }
	        }
	    }

	    if (hasDiscount) {
	        page = new PageImpl<>(filteredProducts, PageRequest.of(pageNum - 1, pageSize), filteredProducts.size());
	    }

	    Map<String, Object> response = new HashMap<>();
	    response.put("page", page);
	    response.put("productDiscountMap", productDiscountMap);
	    response.put("roundedDiscountedPriceMap", roundedDiscountedPriceMap);
	    response.put("averageRatings", averageRatings);
	    response.put("reviewCounts", reviewCounts);

	    return ResponseEntity.ok(response);
	}


	
	// 進入單一商品頁
    @GetMapping("/public/Products/{productId}")
	public String showProductDetails(
	        @PathVariable Integer productId, 
	        HttpSession session, Model model, 
	        @RequestParam(defaultValue = "0") int page) {
    	
	    
	    // 取得當前會員ID
	    Integer memberId = null;
	    if (session.getAttribute("loggedInMember") != null) {
	        memberId = ((MembersBean) session.getAttribute("loggedInMember")).getMemberId();
	        
	        // 點集收藏時會新增這個session標記，防止連續出現2次進入商品頁的log
	        Boolean skipLogAction = (Boolean) session.getAttribute("skipLogAction");
	        if (skipLogAction == null || !skipLogAction) {
	            memberActionLogService.logAction(memberId, "查看商品頁", productId);
	        } else {
	            // 移除標記，以便下次正常記錄
	            session.removeAttribute("skipLogAction");
	        }
	    }
	    Map<String, Object> productDetails = productService.getProductDetails(productId);
	    List<ProductReview> productReviews = productReviewService.findProductReviewsByProductId(productId);
	    Page<ProductReview> productReviewsForPage = productReviewService.findProductReviewsByProductId(productId, page, 4);
	    
	 // 檢查是否已經評價過
	    boolean hasReviewed = false;
	    boolean showForUser = false;

	    if (memberId != null) {
	    	// 檢查是否以評價過
	        for (ProductReview review : productReviews) {
	            if (review.getMember().getMemberId().equals(memberId)) {
	                hasReviewed = true;
	                break;
	            }
	        }
	        
	     // 檢查是否購買且訂單狀態為"已送達"
	        List<OrderDetails> orderDetailsList = orderDetailRepo.findOrderDetailsByProductIdAndMemberId(productId, memberId);
	        if (!orderDetailsList.isEmpty()) {
	            showForUser = true;
	        }
	    }
	    
	    List<Integer> recentProducts = (List<Integer>)session.getAttribute("recentProducts");
	    if (recentProducts == null) {
	        recentProducts = new ArrayList<>();
	    }
	    
	    if (recentProducts.contains(productId)) {
	        recentProducts.remove(productId);
	    }
	    recentProducts.add(0, productId);
	    if (recentProducts.size() > 5) {
	        recentProducts.remove(recentProducts.size() - 1); // 只保存5個瀏覽紀錄
	    }
	    session.setAttribute("recentProducts", recentProducts);

	    Product product = (Product) productDetails.get("product");
	    List<ProductDiscount> productDiscounts = productDiscountService.findByProductId(productId);
	    Map<String, Object> discountInfo = new HashMap<>();
	    if (!productDiscounts.isEmpty() && productDiscounts.get(0).getIsActive() == 1) { // 確認折扣是否上架
	        ProductDiscount discount = productDiscounts.get(0);
	        double discountedPrice = product.getProductPrice() * (1 - discount.getDiscountPercentage() / 100.0);
	        discountInfo.put("discountPercentage", discount.getDiscountPercentage());
	        discountInfo.put("discountedPrice", Math.round(discountedPrice));
	    }

	    model.addAttribute("product", product);
	    model.addAttribute("recommend5Products", productDetails.get("recommend5Products"));
	    model.addAttribute("allProductReviews", productDetails.get("allProductReviews"));
	    model.addAttribute("twoProductReviews", productDetails.get("twoProductReviews"));
	    model.addAttribute("productReviews", productReviews);
	    model.addAttribute("averageRatings", productDetails.get("averageRatings"));
	    model.addAttribute("reviewCounts", productDetails.get("reviewCounts"));
	    model.addAttribute("hasReviewed", hasReviewed);
	    model.addAttribute("showForUser", showForUser);
	    model.addAttribute("productReviewsContent", productReviewsForPage.getContent()); // 獲取當前頁的評論
	    model.addAttribute("totalPages", productReviewsForPage.getTotalPages()); // 總頁數
	    model.addAttribute("discountInfo", discountInfo);

	    return "front/pinhong/SingleProduct";
	}

	
	// 商品評分、評分數的Api
	@ResponseBody
    @GetMapping("/productRatings/api")
    public ResponseEntity<Map<String, Object>> getProductRatings() {
        Map<String, Object> result = new HashMap<>();
        result.put("averageRatings", productService.getAverageRatings());
        result.put("reviewCounts", productService.getReviewCounts());
        return ResponseEntity.ok(result);
    }
	
	// 新增評論
	@PostMapping("/public/InsertProductReview")
	public String productReviewInsert(
			@RequestParam Integer productId,
			@RequestParam Integer memberId,
			@RequestParam String reviewContent, 
			@RequestParam Integer stars, 
			Model m
			) throws IOException{
		
		// 因為前端已經可以知道是否評價過，不寫IF條件擋有評論過的會員了
		productReviewService.insertProductReview(productId, memberId, reviewContent, stars);

	    return "redirect:/public/Products/" + productId;
	};
	
	// 最近瀏覽的商品
	@GetMapping("/public/recentProducts")
	public String showRecentProducts(HttpSession session, Model model) {
	    // 沒登入退回登入頁
	    if (session.getAttribute("loggedInMember") == null) {
	        return "redirect:/public/frontLoginMain";
	    }

	    // 從 session 中取得最近查看的ProductId List
	    List<Integer> recentProductIds = (List<Integer>) session.getAttribute("recentProducts");
	    List<Product> recentProducts = new ArrayList<>();

	    // 查最近看的商品
	    if (recentProductIds != null) {
	        for (Integer productId : recentProductIds) {
	            Product product = productService.findProductById(productId);
	            if (product != null) {
	                recentProducts.add(product);
	                // 如果有近期瀏覽商品被刪除，product會 == null，所以只顯示沒被刪除的商品，不然會跳錯	                
	            } 
	        }
	    }

	    // 所有商品的平均評分和評論總數
	    Map<Integer, Double> averageRatings = productService.getAverageRatings();
	    Map<Integer, Integer> reviewCounts = productService.getReviewCounts();
	    Map<Integer, ProductDiscount> productDiscountMap = new HashMap<>();
	    Map<Integer, Integer> roundedDiscountedPriceMap = new HashMap<>();
	    
	    for (Product product : recentProducts) {
	        List<ProductDiscount> productDiscounts = productDiscountService.findByProductId(product.getProductId());
	        if (!productDiscounts.isEmpty()&& productDiscounts.get(0).getIsActive() == 1) { // 確認折扣是否上架
	            ProductDiscount discount = productDiscounts.get(0);
	            productDiscountMap.put(product.getProductId(), discount);
	            double discountedPrice = product.getProductPrice() * (1 - discount.getDiscountPercentage() / 100.0);
	            roundedDiscountedPriceMap.put(product.getProductId(), (int) Math.round(discountedPrice));
	        } else {
	            productDiscountMap.put(product.getProductId(), null); // 如果没有折扣，映射到null
	        }
	    }

	    // 為每個最近查看的商品 add 平均評分和評論總數
	    List<Map<String, Object>> productsWithRatings = new ArrayList<>();
	    for (Product product : recentProducts) {
	        Map<String, Object> productData = new HashMap<>();
	        productData.put("product", product);
	        productData.put("averageRating", averageRatings.getOrDefault(product.getProductId(), 0.0));
	        productData.put("reviewCount", reviewCounts.getOrDefault(product.getProductId(), 0));
	        productData.put("productDiscount", productDiscountMap.get(product.getProductId()));
	        productData.put("discountedPrice", roundedDiscountedPriceMap.get(product.getProductId()));
	        productsWithRatings.add(productData);
	    }

	    // 如果没有最近查看的商品，傳到前端
	    if (productsWithRatings.isEmpty()) {
	        model.addAttribute("message", "您目前沒有查看過任何商品，點擊按鈕可跳至商品頁");
	    } 
	    
	    model.addAttribute("recentProducts", productsWithRatings);
	    return "front/pinhong/recentProducts";
	}

	
	// 找該商品所有的評論 for 後台getAll
	@GetMapping("/private/Product/Reviews")
	public String findProductReviewsByProductId(@RequestParam Integer productId, Model m){
		
		List<ProductReview> reviews = productReviewService.findProductReviewsByProductId(productId);
		m.addAttribute("reviews", reviews);
		
	    return "back/pinhong/FindProductReviews";
	}
	
	@DeleteMapping("/private/Product/Review/delete")
	@ResponseBody
	public String deleteProductReviewByReviewId(@RequestParam Integer reviewId) {
	    productReviewService.deleteProductReviewByReviewId(reviewId);
	    return "success";
	}
	
	//  找該商品所有的評論 for 前台
	@GetMapping("/public/Products/{productId}/Reviews")
	public String findProductReviewsByProductIdForFront(@PathVariable Integer productId, Model model) {
	    List<ProductReview> reviews = productReviewService.findProductReviewsByProductId(productId);
	    model.addAttribute("reviews", reviews);
	    Product product = productService.findProductById(productId);
	    model.addAttribute("product", product);
	    Map<Integer, Double> averageRatings = productService.getAverageRatings();
	    Map<Integer, Integer> reviewCounts = productService.getReviewCounts();
	    
	    model.addAttribute("averageRatings", averageRatings.get(productId));
	    model.addAttribute("reviewCounts", reviewCounts.get(productId));
	    
	    return "front/pinhong/productReviews"; 
	} 
}
	