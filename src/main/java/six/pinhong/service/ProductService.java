package six.pinhong.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.liang.model.ProductDiscount;
import six.liang.service.ProductDiscountService;
import six.pinhong.model.Product;
import six.pinhong.model.ProductImage;
import six.pinhong.model.ProductImageRepository;
import six.pinhong.model.ProductRepository;
import six.pinhong.model.ProductReview;

@Service
@Transactional
public class ProductService {
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ProductImageRepository productImageRepo;
	
	@Autowired ProductReviewService productReviewService;
	
	@Autowired
	private ProductDiscountService productDiscountService;
	
	// 找全部	
	public List<Product> findAll(){
		return productRepo.findAll();
	}
	
	// 刪除by ID	 
	public void deleteById(Integer id) {
		productRepo.deleteById(id);
	}
	
	// 找單筆商品 by ID	 
	public Product findProductById(Integer id) {
		Optional<Product> optional = productRepo.findById(id);
		
		if (optional.isPresent()) {
			Product product = optional.get();
			return product;
		}
		
		return null;
	}
	
	// 找單筆圖片 BY ID
	public ProductImage findImageById(Integer id) {
		Optional<ProductImage> optional = productImageRepo.findById(id);
		
		if (optional.isPresent()) {
			ProductImage productImage = optional.get();
			return productImage;
		}
		
		return null;
	}
	
	// 商品 更新+新增	
	public Product insertProduct(Product product) {
		return productRepo.save(product);
	}

	
	// shop.html - 前台頁碼、查詢
	
	public Page<Product> findByPage(String searchTerm, String productType, int pageNum, int pageSize, String sortField) {
	    Sort sort;
	    if (sortField != null && !sortField.equals("default")) {
	        switch (sortField) {
	            case "productId":
	                sort = Sort.by(Sort.Direction.DESC, "productId");
	                break;
	            case "productPrice":
	                sort = Sort.by(Sort.Direction.ASC, "productPrice");
	                break;
	            default:
	                sort = Sort.unsorted();
	        }
	    } else {
	        sort = Sort.unsorted();
	    }

	    Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

	    if (!searchTerm.isEmpty() && !productType.isEmpty()) {
	        return productRepo.findByProductNameContainingAndProductTypeContainingAndProductPublished(searchTerm, productType, 1, pageable);
	    } else if (!searchTerm.isEmpty()) {
	        return productRepo.findByProductNameContainingAndProductPublished(searchTerm, 1, pageable);
	    } else if (!productType.isEmpty()) {
	        return productRepo.findByProductTypeContainingAndProductPublished(productType, 1, pageable);
	    } else {
	        return productRepo.findByProductPublished(1, pageable);
	    }
	}


    
	// 隨機五個同類型商品當推薦，且排除目前正在單一商品頁查看的productId
	public List<Product> findSametype5Products(String productType, Integer currentProductId){
		return productRepo.findSametype5Products(productType, currentProductId);
	}

	
	// 找5個庫存數量最多的產品	 
	public List<Product> findTop5ByOrderByProductQuantityDesc() {
		return productRepo.findTop5ByOrderByProductQuantityDesc();
	}
	
	// 找10個最新上架的產品
	public List<Product> findTop10ByOrderByProductIdDesc(){
		return productRepo.findTop10ByOrderByProductIdDesc();
	}
	
	public Map<String, Object> getProductDetails(Integer productId) {
	    Map<String, Object> result = new HashMap<>();

	    Product product = findProductById(productId);
	    result.put("product", product);

	    List<Product> recommend5Products = findSametype5Products(product.getProductType(), productId);

	    // 创建一个包含折扣信息的推荐商品列表
	    List<Map<String, Object>> recommendProductsWithDiscounts = new ArrayList<>();
	    for (Product recommendProduct : recommend5Products) {
	        Map<String, Object> productMap = new HashMap<>();
	        productMap.put("product", recommendProduct);

	        List<ProductDiscount> productDiscounts = productDiscountService.findByProductId(recommendProduct.getProductId());
	        if (!productDiscounts.isEmpty() && productDiscounts.get(0).getIsActive() == 1) {
	            ProductDiscount discount = productDiscounts.get(0);
	            double discountedPrice = recommendProduct.getProductPrice() * (1 - discount.getDiscountPercentage() / 100.0);
	            productMap.put("discountPercentage", discount.getDiscountPercentage());
	            productMap.put("discountedPrice", Math.round(discountedPrice));
	        } else {
	            productMap.put("discountPercentage", null);
	            productMap.put("discountedPrice", recommendProduct.getProductPrice());
	        }

	        recommendProductsWithDiscounts.add(productMap);
	    }

	    result.put("recommend5Products", recommendProductsWithDiscounts);

	    List<ProductReview> allProductReviews = productReviewService.findProductReviewsByProductId(productId);
	    result.put("allProductReviews", allProductReviews);

	    List<ProductReview> twoProductReviews = productReviewService.findTop2ByProductIdOrderByReviewTimeDesc(productId);
	    result.put("twoProductReviews", twoProductReviews);

	    // Calculate average ratings
	    Map<Integer, Double> averageRatings = new HashMap<>();
	    List<ProductReview> allReviews = productReviewService.findAll();
	    Map<Integer, List<ProductReview>> reviewsByProductId = new HashMap<>();
	    for (ProductReview review : allReviews) {
	        int productIdKey = review.getProduct().getProductId();

	        if (!reviewsByProductId.containsKey(productIdKey)) {
	            reviewsByProductId.put(productIdKey, new ArrayList<>());
	        }
	        reviewsByProductId.get(productIdKey).add(review);
	    }

	    for (Map.Entry<Integer, List<ProductReview>> entry : reviewsByProductId.entrySet()) {
	        int pid = entry.getKey();
	        List<ProductReview> reviews = entry.getValue();
	        double totalStars = reviews.stream().mapToDouble(ProductReview::getStars).sum();
	        double averageRating = totalStars / reviews.size();
	        DecimalFormat df = new DecimalFormat("#.#");
	        averageRating = Double.parseDouble(df.format(averageRating));
	        averageRatings.put(pid, averageRating);
	    }
	    result.put("averageRatings", averageRatings);

	    // Create a map for review counts
	    Map<Integer, Integer> reviewCounts = new HashMap<>();
	    for (Product recommendedProduct : recommend5Products) {
	        int recommendedProductId = recommendedProduct.getProductId();
	        int reviewCount = productReviewService.findProductReviewsByProductId(recommendedProductId).size();
	        reviewCounts.put(recommendedProductId, reviewCount);
	    }
	    result.put("reviewCounts", reviewCounts);

	    return result;
	}
    
    // shop.html所有商品的平均評分 key = productId, value = 平均評分
    public Map<Integer, Double> getAverageRatings() {
        Map<Integer, Double> averageRatings = new HashMap<>();
        List<ProductReview> allReviews = productReviewService.findAll();
        Map<Integer, List<ProductReview>> reviewsByProductId = new HashMap<>();

        for (ProductReview review : allReviews) {
        	Integer productIdKey = review.getProduct().getProductId();
        	
            if (!reviewsByProductId.containsKey(productIdKey)) {
                reviewsByProductId.put(productIdKey, new ArrayList<>());
            }
            reviewsByProductId.get(productIdKey).add(review);
        }

        for (Map.Entry<Integer, List<ProductReview>> entry : reviewsByProductId.entrySet()) {
        	Integer pid = entry.getKey();
            List<ProductReview> reviews = entry.getValue();
            double totalStars = reviews.stream().mapToDouble(ProductReview::getStars).sum();
            double averageRating = totalStars / reviews.size();
            DecimalFormat df = new DecimalFormat("#.#");
            averageRating = Double.parseDouble(df.format(averageRating));
            averageRatings.put(pid, averageRating);
        }

        return averageRatings;
    }

 // shop.html所有商品的平均評分 key = productId, value = 評論總數
    public Map<Integer, Integer> getReviewCounts() {
        Map<Integer, Integer> reviewCounts = new HashMap<>();
        List<ProductReview> allReviews = productReviewService.findAll();
        
        for (ProductReview review : allReviews) {
        	Integer productIdKey = review.getProduct().getProductId();
            reviewCounts.put(productIdKey, reviewCounts.getOrDefault(productIdKey, 0) + 1);
        }

        return reviewCounts;
    }
    
    public Map<String, Object> getHomePageDetails() {
        Map<String, Object> result = new HashMap<>();

        List<Product> products = findTop5ByOrderByProductQuantityDesc();
        result.put("products", products);

        List<Product> new10Products = findTop10ByOrderByProductIdDesc();
        result.put("new10Products", new10Products);

        // 計算所有商品的平均評分
        Map<Integer, Double> averageRatings = new HashMap<>();
        Map<Integer, Integer> reviewCounts = new HashMap<>();
        List<ProductReview> allReviews = productReviewService.findAll();

        // 處理 products
        for (Product product : products) {
            calculateProductRating(product, allReviews, averageRatings, reviewCounts);
        }

        // 處理 new10Products
        for (Product product : new10Products) {
            if (!averageRatings.containsKey(product.getProductId())) {
                calculateProductRating(product, allReviews, averageRatings, reviewCounts);
            }
        }

        result.put("averageRatings", averageRatings);
        result.put("reviewCounts", reviewCounts);

        return result;
    }
    
    // 計算評論數量
    private void calculateProductRating(Product product, List<ProductReview> allReviews, 
                                        Map<Integer, Double> averageRatings, 
                                        Map<Integer, Integer> reviewCounts) {
        int productId = product.getProductId();
        List<ProductReview> productReviews = new ArrayList<>();

        for (ProductReview review : allReviews) {
            if (review.getProduct().getProductId() == productId) {
                productReviews.add(review);
            }
        }

        if (!productReviews.isEmpty()) {
            double totalStars = 0;
            for (ProductReview review : productReviews) {
                totalStars += review.getStars();
            }
            double averageRating = totalStars / productReviews.size();
            DecimalFormat df = new DecimalFormat("#.#");
            averageRating = Double.parseDouble(df.format(averageRating));
            averageRatings.put(productId, averageRating);
            reviewCounts.put(productId, productReviews.size());
        } else {
            averageRatings.put(productId, 0.0);
            reviewCounts.put(productId, 0);
        }
    }
}