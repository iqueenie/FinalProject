package six.queenie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.pinhong.model.Product;
import six.pinhong.service.ProductService;

@Service
public class CartService {
	
	@Autowired
	private ProductService pService;
	
	public Product addToCart(Integer productId) {
		return pService.findProductById(productId);
		
	}

}
