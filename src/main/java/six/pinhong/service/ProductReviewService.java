package six.pinhong.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	// 商品頁顯示兩則最新的評論
	public List<ProductReview> findTop2ByProductIdOrderByReviewTimeDesc(Integer productId){
		return productReviewRepo.findTop2ByProductIdOrderByReviewTimeDesc(productId);
	}
	
//	// 商品 更新+新增	
//	public Product insertProduct(Product product) {
//		return productRepo.save(product);
//	}
//
//	
//	// shop.html - 前台頁碼、查詢
//	
//    public Page<Product> findByPage(String searchTerm, String productType, Integer pageNum, String sortField, String sortDir, int pageSize) {
//        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.fromString(sortDir), sortField);
//
//        if ((searchTerm == null || searchTerm.trim().isEmpty()) && (productType == null || productType.trim().isEmpty())) {
//            return productRepo.findAllPublished(pageable); // 如果搜索词和商品类型都为空，返回所有产品
//        }
//
//        return productRepo.searchProducts(searchTerm.trim(), productType, pageable);
//    }
//    
//	// 隨機五個同類型商品當推薦，且排除目前正在單一商品頁查看的productId
//	public List<Product> findSametype5Products(String productType, Integer currentProductId){
//		return productRepo.findSametype5Products(productType, currentProductId);
//	}
//
//	
//	// 找5個庫存數量最多的產品	 
//	public List<Product> findTop5ByOrderByProductQuantityDesc() {
//		return productRepo.findTop5ByOrderByProductQuantityDesc();
//	}
//	
//	// 找10個最新上架的產品
//	public List<Product> findTop10ByOrderByProductIdDesc(){
//		return productRepo.findTop10ByOrderByProductIdDesc();
//	}
}