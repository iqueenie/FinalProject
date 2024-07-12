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

@Service
@Transactional
public class ProductService {
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ProductImageRepository productImageRepo;
	
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
	
    public Page<Product> findByPage(String searchTerm, String productType, Integer pageNum, String sortField, String sortDir, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.Direction.fromString(sortDir), sortField);

        if ((searchTerm == null || searchTerm.trim().isEmpty()) && (productType == null || productType.trim().isEmpty())) {
            return productRepo.findAllPublished(pageable); // 如果搜索词和商品类型都为空，返回所有产品
        }

        return productRepo.searchProducts(searchTerm.trim(), productType, pageable);
    }

	
	// 找5個庫存數量最多的產品	 
	public List<Product> findTop5ByOrderByProductQuantityDesc() {
		return productRepo.findTop5ByOrderByProductQuantityDesc();
	}
	
	// 找10個最新上架的產品
	public List<Product> findTop10ByOrderByProductIdDesc(){
		return productRepo.findTop10ByOrderByProductIdDesc();
	}
}