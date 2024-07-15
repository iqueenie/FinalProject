package six.queenie.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import six.liang.model.AmountDiscount;
import six.liang.model.AmountDiscountRepository;
import six.liang.model.ProductDiscount;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.model.ProductDiscountRepository;

@Service
public class CartService {

    @Autowired
    private AmountDiscountRepository atRepository;

    @Autowired
    private ProductDiscountRepository pdRepository;
   
    public Integer getAmountDiscount(Integer total, Integer amountDiscountId) {
        if (amountDiscountId == null) {
            return 0;
        }

        Optional<AmountDiscount> mdOptional = atRepository.findById(amountDiscountId);
        if (mdOptional.isPresent()) {
            AmountDiscount md = mdOptional.get();
            Integer discountPercentage = md.getDiscountPercentage();

            if (discountPercentage != 0) {
                return (int) Math.floor(total * discountPercentage / 100.0);
            }
        }

        return 0;
    }

    public Integer getProductDiscount(Integer productDiscountId, Date orderDate) {
        if (productDiscountId == null) {
            return 0;
        }

        Integer discountPercentage = 0;
        Optional<ProductDiscount> pdOptional = pdRepository.findById(productDiscountId);
        if (pdOptional.isPresent()) {
            ProductDiscount pd = pdOptional.get();
            discountPercentage = pd.getDiscountPercentage();
        }
        return discountPercentage;
    }

    public List<Map<String, Object>> getProductDetails(List<Product> cartItems) {
        List<Map<String, Object>> productDetails = new ArrayList<>();
        for (Product product : cartItems) {
            Map<String, Object> details = new HashMap<>();
            details.put("productId", product.getProductId());
            details.put("productName", product.getProductName());
            details.put("productPrice", product.getProductPrice());
            details.put("productDescription", product.getProductDescription());

            if (product.getProductImage() != null) {
                String imageUrl = "/ProductPhoto?productId=" + product.getProductId();
                details.put("imageUrl", imageUrl);
            }

            productDetails.add(details);
        }
        return productDetails;
    }

    public Map<String, Integer> calculateCartDetails(List<Product> cartItems, Map<Integer, Integer> productQuantities) {
    	
    	Integer total = 0;
        Integer sumTotal = 0;

        LocalDate currentDate = LocalDate.now();
        Date orderDate = Date.valueOf(currentDate);

        List<Map<String, Object>> productDetails = new ArrayList<>();

        for (int i = 0; i < cartItems.size(); i++) {
            Product product = cartItems.get(i);
            Integer productId = product.getProductId();
            Integer productPrice = product.getProductPrice();
            Integer quantity = productQuantities.getOrDefault(productId, 1); 

            Integer productDiscountId = pdRepository.findProductDiscountId(productId, orderDate);
            Integer productDiscount = getProductDiscount(productDiscountId, orderDate);

            Integer subTotal;
            if (productDiscountId != null && productDiscountId != 0) {
                double discountAmount = productPrice * productDiscount / 100.0 * quantity;
                subTotal = productPrice * quantity - (int) Math.floor(discountAmount);
            } else {
                subTotal = productPrice * quantity;
            }

            total += subTotal;
            sumTotal += productPrice * quantity;

            Map<String, Object> details = new HashMap<>();
            details.put("productId", productId);
            details.put("productName", product.getProductName());
            details.put("productPrice", productPrice);
            details.put("productDescription", product.getProductDescription());
            details.put("quantity", productQuantities.get(productId));
            productDetails.add(details);
        }

        Integer amountDiscountId = atRepository.findAmountDiscountId(total, orderDate);
        Integer amountDiscount = getAmountDiscount(total, amountDiscountId);
        Integer discountMoney = amountDiscount + (sumTotal - total);
        Integer finalAmount = total - discountMoney;

        Map<String, Integer> cartDetails = new HashMap<>();
        cartDetails.put("total", total);
        cartDetails.put("sumTotal", sumTotal);
        cartDetails.put("discountMoney", discountMoney);
        cartDetails.put("finalAmount", finalAmount);
        

        return cartDetails;
    }
    
   
}
