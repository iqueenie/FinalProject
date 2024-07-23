package six.pinhong.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.pinhong.model.Product;
import six.pinhong.model.ProductImage;
import six.pinhong.model.ProductImageRepository;
import six.pinhong.model.ProductRepository;
import six.pinhong.model.ProductReview;
import six.pinhong.model.ProductReviewRepository;

@Service
@Transactional
public class ProductReviewService {
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ProductReviewRepository productReviewRepo;
	
	@Autowired
	private MembersRepository membersRepo;
	
	@Autowired
    private ForbiddenWordService forbiddenWordService;
	
	
	// 找全部	
	public List<ProductReview> findAll(){
		return productReviewRepo.findAll();
	}
	
	// 刪除by ID	 
	public void deleteById(Integer id) {
		productReviewRepo.deleteById(id);
	}
	
	// 找該商品所有的評論
	public List<ProductReview> findProductReviewsByProductId(Integer productId){
		return productReviewRepo.findByProductProductId(productId);
	}
	// 找該商品所有的評論+分頁 for 所有評論使用
	public Page<ProductReview> findProductReviewsByProductId(Integer productId, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    return productReviewRepo.findByProductProductId(productId, pageable);
	}
	
	// 商品頁顯示兩則最新的評論
	public List<ProductReview> findTop2ByProductIdOrderByReviewTimeDesc(Integer productId){
		return productReviewRepo.findTop2ByProductIdOrderByReviewTimeDesc(productId);
	}
	
	public void insertProductReview(Integer productId, Integer memberId, String reviewContent, Integer stars) {
	    ProductReview review = new ProductReview();
	    // 獲取Product實體
	    Product product = productRepo.findById(productId).orElse(null);
	    review.setProduct(product);
	    
	    // 獲取MembersBean實體
	    MembersBean member = membersRepo.findById(memberId).orElse(null);
	    review.setMember(member);
	    
        // 替換敏感詞彙
        reviewContent = forbiddenWordService.replaceForbiddenWords(reviewContent);
	    review.setReviewContent(reviewContent);
	    review.setStars(stars);
	    review.setReviewTime(new Date()); // 假設你想記錄評論時間

	    productReviewRepo.save(review);
	}
	
	// 刪除 by ReviewId
	public void deleteProductReviewByReviewId(Integer reviewId) {
		productReviewRepo.deleteById(reviewId);
	}

}