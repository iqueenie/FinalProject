package six.liang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import six.liang.model.ProductDiscount;
import six.liang.model.DiscountProductRepository;

import java.util.List;

@Service
@Transactional
public class ProductDiscountService {

    @Autowired
    private DiscountProductRepository discountProductRepo;
    
    @Autowired
    public List<ProductDiscount> findAll() {
        return discountProductRepo.findAll();
    }

    public void insert(ProductDiscount productDiscount) {
        discountProductRepo.save(productDiscount);
    }

    public void deleteById(Integer id) {
        discountProductRepo.deleteById(id);
    }

    public void update(ProductDiscount productDiscount) {
        discountProductRepo.save(productDiscount);
    }

    public ProductDiscount findById(Integer id) {
        return discountProductRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("折扣信息不存在"));
    }
    
    public List<ProductDiscount> findByProductId(Integer productId) {
        return discountProductRepo.findByProductId(productId);
    }
}
