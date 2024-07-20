package six.pinhong.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.pinhong.model.Product;
import six.pinhong.model.ProductFavorite;
import six.pinhong.model.ProductFavoriteRepository;
import six.pinhong.model.ProductRepository;

@Service
@Transactional
public class ProductFavoriteService {
	
    @Autowired
    private ProductFavoriteRepository productFavoriteRepo;
    
	@Autowired
	private ProductRepository productRepo;
		
	@Autowired
	private MembersRepository membersRepo;

    public ProductFavorite getFavoritesByMemberIdAndProductProductId(Integer memberId, Integer productId) {
        return productFavoriteRepo.findByMemberMemberIdAndProductProductId(memberId,productId);
    }
    
    public List<ProductFavorite> getFavoritesByMemberId(Integer memberId){
    	return productFavoriteRepo.findByMemberMemberId(memberId);
    }
    
    public void insertProductFavorite(Integer productId, Integer memberId) {
    	ProductFavorite productFavorite = new ProductFavorite();
	    // 獲取Product實體
	    Product product = productRepo.findById(productId).orElse(null);
	    productFavorite.setProduct(product);
	    
	    // 獲取MembersBean實體
	    MembersBean member = membersRepo.findById(memberId).orElse(null);
	    productFavorite.setMember(member);
	    
    	productFavoriteRepo.save(productFavorite);
    }
    
    public void deleteProductFavoriteByProductId(Integer productId) {
    	productFavoriteRepo.deleteByProductProductId(productId);
    }

}