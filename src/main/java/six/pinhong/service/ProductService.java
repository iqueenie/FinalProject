package six.pinhong.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.pinhong.model.Product;
import six.pinhong.model.ProductRepository;

@Service
@Transactional
public class ProductService {
	
	@Autowired
	private ProductRepository productRepo;
	
	public List<Product> findAll(){
		return productRepo.findAll();
	}
}
