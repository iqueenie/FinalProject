package six.queenie.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.liang.model.AmountDiscount;
import six.liang.model.AmountDiscountRepository;
import six.liang.model.ProductDiscount;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.model.OrdersRepository;
import six.queenie.model.ProductDiscountRepository;

@Service
public class CartService {
    
    @Autowired
    private AmountDiscountRepository atRepository;
    
    @Autowired
    private ProductDiscountRepository pdRepository;
    
    @Autowired
    private ProductService pService;
    
    @Autowired
    private OrdersRepository orderRepo;
    
    
    
    public Integer getAmountDiscount(Integer total, Integer amountDiscountId) {
        if (amountDiscountId == null) {
            return total; 
        }

        Integer discountPercentage = 0;

        Optional<AmountDiscount> mdOptional = atRepository.findById(amountDiscountId);
        if (mdOptional.isPresent()) {
            AmountDiscount md = mdOptional.get();
            discountPercentage = md.getDiscountPercentage();
        }

        if (discountPercentage <= 0) {
            return total;
        }

        Integer discountAmount = (int)Math.floor(total * discountPercentage / 100.0);
        return discountAmount;
    }

	    
	    public Integer getProductDiscount(Integer  productDiscountId) {
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
            details.put("productQuantity", product.getProductQuantity());
                     
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

        // 遍歷購物車中的每個產品，計算折扣和小計
        for (int i = 0; i < cartItems.size(); i++) {
            Product product = cartItems.get(i);
            Integer productPrice = product.getProductPrice();
            Integer quantity = (quantities.isEmpty() || i >= quantities.size()) ? 1 : quantities.get(i);
            Integer subTotal = productPrice * quantity;
            total += subTotal;
            sumTotal += subTotal;

            // 取得當前訂單日期
            LocalDate localDate = LocalDate.now();
            Date orderDate = Date.valueOf(localDate);

            // 查找產品折扣
            Integer productDiscountId = pdRepository.findProductDiscountId(product.getProductId(), orderDate);
            Integer productDiscount = getProductDiscount(productDiscountId);
            productDiscountMap.put(product.getProductId(), productDiscount);

            // 構建產品詳情列表供前端使用
            Map<String, Object> details = new HashMap<>();
            details.put("productId", product.getProductId());
            details.put("productName", product.getProductName());
            details.put("productPrice", productPrice);
            details.put("productDescription", product.getProductDescription());
            details.put("quantity", quantity);
            productDetails.add(details);
        }

        // 查找總訂單折扣
        Integer amountDiscountId = atRepository.findAmountDiscountId(total, Date.valueOf(LocalDate.now()));
        Integer amountDiscount = getAmountDiscount(total, amountDiscountId);

        // 計算總折扣
        Integer totalDiscount = productDiscountMap.values().stream().mapToInt(Integer::intValue).sum() + amountDiscount;
        Integer finalAmount = total - totalDiscount;
        		// 將結果存儲在 session 和 model 屬性中
        session.setAttribute("total", total);
        session.setAttribute("totalDiscount", totalDiscount);
        session.setAttribute("productDiscountMap", productDiscountMap);
        session.setAttribute("finalAmount", finalAmount);
        session.setAttribute("productDetails", productDetails);
        
        System.out.println("Total amount of the cart: " + total);
        System.out.println("Total discount applied: " + totalDiscount);
        System.out.println("amountDiscount: " + amountDiscount);
        System.out.println("amountDiscount: " + amountDiscountId);
        System.out.println("sumTotal: " + sumTotal);
        System.out.println("final: " + finalAmount);
        
        if (model != null) {
            model.addAttribute("total", total);
            model.addAttribute("totalDiscount", totalDiscount);
            model.addAttribute("productDiscountMap", productDiscountMap);
            model.addAttribute("finalAmount", finalAmount);
            model.addAttribute("productDetails", productDetails);
        }
    }
    
    
//    public void orderCheckedOut(MembersBean member, List<Product> cartItems, boolean isCheckedOut, HttpSession session) {
//        if (isCheckedOut) {
//            Orders newOrder = new Orders();
//            newOrder.setMembers(member);
//
//            Set<OrderDetails> orderDetails = new HashSet<>(); 
//            for (Product product : cartItems) {
//                OrderDetails detail = new OrderDetails();
//                detail.setProduct(product);
//                detail.setOrders(newOrder);
//                detail.setQuantity(product.getProductQuantity());
//                orderDetails.add(detail);
//            }
//
//            newOrder.setDetails(orderDetails);
//
//            calculateCartDiscounts(cartItems, new ArrayList<>(), session, null);
//
//            orderRepo.save(newOrder);
//
//            session.removeAttribute("cartItems");
//        } else {
//          
//            session.setAttribute("cartItems", cartItems);
//        }
//    }

}