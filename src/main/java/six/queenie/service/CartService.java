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
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import six.liang.model.AmountDiscount;
import six.liang.model.AmountDiscountRepository;
import six.liang.model.ProductDiscount;
import six.pinhong.model.Product;
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

	    
	    public Integer getProductDiscount(Integer  productDiscountId, Date orderDate) {
	    	if (productDiscountId == null) {
	            return 0; 
	        }
	        Integer discountPercentage=0;
	          
	        Optional<ProductDiscount> pdOptional = pdRepository.findById(productDiscountId);
	        if(pdOptional.isPresent()) {
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

    public void calculateCartDiscounts(List<Product> cartItems, List<Integer> quantities, HttpSession session, Model model) {
        Integer total = 0;
        Integer sumTotal = 0;
        Map<Integer, Integer> productDiscountMap = new HashMap<>();
        List<Map<String, Object>> productDetails = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        Date orderDate = Date.valueOf(currentDate);

        System.out.println("Current Order Date: " + orderDate);

        for (int i = 0; i < cartItems.size(); i++) {
            Product product = cartItems.get(i);
            Integer productId = product.getProductId();
            Integer productPrice = product.getProductPrice();
            Integer quantity = (quantities.isEmpty() || i >= quantities.size()) ? 1 : quantities.get(i);
            Integer productDiscountId = pdRepository.findProductDiscountId(productId, orderDate);
            System.out.println("Product Discount ID: " + productDiscountId);

            Integer productDiscount = getProductDiscount(productDiscountId, orderDate);
            System.out.println("Product Discount: " + productDiscount);
            Integer subTotal;
            if (productDiscountId != null && productDiscountId != 0) {
                double result = productPrice * productDiscount / 100.0 * quantity;
                subTotal = productPrice * quantity- (int) Math.floor(result);
            } else {
                subTotal = productPrice * quantity;
            }
            total += subTotal;
            sumTotal += productPrice * quantity;

            System.out.println("Product ID: " + productId);
            System.out.println("Product Price: " + productPrice);
            System.out.println("Quantity: " + quantity);
            System.out.println("Subtotal: " + subTotal);


            Map<String, Object> details = new HashMap<>();
            details.put("productId", productId);
            details.put("productName", product.getProductName());
            details.put("productPrice", productPrice);
            details.put("productDescription", product.getProductDescription());
            details.put("quantity", quantity);
            productDetails.add(details);
        }
        System.out.println("Calculating amount discount for total: " + total + " and order date: " + orderDate);
        Integer amountDiscountId = atRepository.findAmountDiscountId(total, orderDate);
        System.out.println("Amount Discount ID: " + amountDiscountId);

        Integer amountDiscount = getAmountDiscount(total, amountDiscountId);
        System.out.println("Amount Discount: " + amountDiscount);

       
        Integer totalDiscount = sumTotal - amountDiscount;
        System.out.println("Total After Amount Discount: " + totalDiscount );
       
        Integer finalAmount = total - totalDiscount;
        System.out.println("Final Amount: " + finalAmount);

        session.setAttribute("total", total);
        session.setAttribute("totalDiscount", totalDiscount);
        session.setAttribute("productDiscountMap", productDiscountMap);
        session.setAttribute("finalAmount", finalAmount);
        session.setAttribute("productDetails", productDetails);

        if (model != null) {
            model.addAttribute("total", total);
            model.addAttribute("totalDiscount", totalDiscount);
            model.addAttribute("productDiscountMap", productDiscountMap);
            model.addAttribute("finalAmount", finalAmount);
            model.addAttribute("productDetails", productDetails);
        }
    }

}

