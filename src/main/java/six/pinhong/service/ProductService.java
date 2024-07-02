package six.pinhong.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	
	public List<Product> findAll(){
		return productRepo.findAll();
	}
	
	public void deleteById(Integer id) {
		productRepo.deleteById(id);
	}
	
	public Product findProductById(Integer id) {
		Optional<Product> optional = productRepo.findById(id);
		
		if (optional.isPresent()) {
			Product product = optional.get();
			return product;
		}
		
		return null;
	}
	
	public ProductImage findImageById(Integer id) {
		Optional<ProductImage> optional = productImageRepo.findById(id);
		
		if (optional.isPresent()) {
			ProductImage productImage = optional.get();
			return productImage;
		}
		
		return null;
	}
	
	// 商品更新
	public Product updateProduct(Product updateBean, Integer id) {
		Optional<Product> optional = productRepo.findById(id);
		
		if (optional.isPresent()) {
			Product product = optional.get();
			
			product.setProductName(updateBean.getProductName());
			product.setProductType(updateBean.getProductType());
			product.setProductCost(updateBean.getProductCost());
			product.setProductPrice(updateBean.getProductPrice());
			product.setProductExpirydate(updateBean.getProductExpirydate());
			product.setProductDescription(updateBean.getProductDescription());
			product.setProductPublished(updateBean.getProductPublished());

			return product;
		}
			return null;
	}
	
	
	// 商品圖片更新
	public ProductImage updateProductImage(ProductImage updateBean, Integer id) {
		Optional<ProductImage> optional = productImageRepo.findById(id);
		
		if (optional.isPresent()) {
			ProductImage productImage = optional.get();
			
			productImage.setImageUrl(updateBean.getImageUrl());

			
			return productImage;
		}
		return null;
	}
	
	public Product insertProduct(Product product) {
		return productRepo.save(product);
	}
	
	// 商品更新
    @Transactional
    public Product updateProduct(Product updateBean) {
    	return productRepo.save(updateBean);
    }
    
    // 商品圖片更新
    @Transactional
    public ProductImage updateProductImage(ProductImage updateBean) {
    	return productImageRepo.save(updateBean);
    }
	
}
