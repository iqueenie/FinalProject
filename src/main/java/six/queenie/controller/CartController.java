package six.queenie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.service.CartService; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class CartController {

    private static final String SESSION_CART = "sessionCart";
    private static final String SESSION_PRODUCT_QUANTITIES = "productQuantities";
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;
    
    @GetMapping("/public/GetAllCart")
    public String showCart(Model model, HttpSession session) {
        List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
        Map<Integer, Integer> productQuantities = (Map<Integer, Integer>) session.getAttribute(SESSION_PRODUCT_QUANTITIES);
        
        if (cartItems == null) {
            return "front/queenie/cart";
        }
        Map<String, Integer> cartDetails = cartService.calculateCartDetails(cartItems, productQuantities);

        session.setAttribute("total", cartDetails.get("total"));
        session.setAttribute("sumTotal", cartDetails.get("sumTotal"));
        session.setAttribute("discountMoney", cartDetails.get("discountMoney"));
        session.setAttribute("finalAmount", cartDetails.get("finalAmount"));
        session.setAttribute("quantity", cartDetails.get("quantity"));
        session.setAttribute("subTotal", cartDetails.get("subTotal"));
        
        model.addAttribute("total", cartDetails.get("total"));
        model.addAttribute("sumTotal", cartDetails.get("sumTotal"));
        model.addAttribute("discountMoney", cartDetails.get("discountMoney"));
        model.addAttribute("finalAmount", cartDetails.get("finalAmount"));
        model.addAttribute("subTotal", cartDetails.get("subTotal"));

        return "front/queenie/cart";
    }

    @PostMapping("/public/update-cart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(@RequestBody Map<String, Object> requestData, HttpSession session) {
        // 從 session 中獲取購物車項目和數量
        List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
        Map<Integer, Integer> productQuantities = (Map<Integer, Integer>) session.getAttribute(SESSION_PRODUCT_QUANTITIES);

        List<Map<String, Object>> updatedCartItems = (List<Map<String, Object>>) requestData.get("cartItems");
   
        if (updatedCartItems != null) {
            for (Map<String, Object> item : updatedCartItems) {
                Integer productId = (Integer) item.get("productId");
                Integer quantity = (Integer) item.get("quantity");
                if (productId != null && quantity != null) {
                    productQuantities.put(productId, quantity);
                }
            }
        }

        Map<String, Integer> cartDetails = cartService.calculateCartDetails(cartItems, productQuantities);

        // 更新 session 中的總金額、折扣金額和最終支付金額
        session.setAttribute("sumTotal", cartDetails.get("sumTotal"));
        session.setAttribute("discountMoney", cartDetails.get("discountMoney"));
        session.setAttribute("finalAmount", cartDetails.get("finalAmount"));
        session.setAttribute("quantity", cartDetails.get("quantity"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("cartItems", cartItems);
        response.put("productQuantities", productQuantities);
        response.put("sumTotal", cartDetails.get("sumTotal"));
        response.put("discountMoney", cartDetails.get("discountMoney"));
        response.put("finalAmount", cartDetails.get("finalAmount"));

        return ResponseEntity.ok(response);
    }



	@GetMapping("/public/product/{productId}")
    public String getProductPage(@PathVariable("productId") Integer productId, Model model) {
        Product product = productService.findProductById(productId);
        model.addAttribute("product", product);
        return "/front/index";
    }

	@PostMapping("/public/addToCart")
	@ResponseBody
	public String addToCart(@RequestParam("productId") Integer productId,
	                        @RequestParam("quantity") Integer quantity,
	                        HttpSession session) {
	    List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
	    Map<Integer, Integer> productQuantities = (Map<Integer, Integer>) session.getAttribute(SESSION_PRODUCT_QUANTITIES);
	    Product product = productService.findProductById(productId);

	    if (cartItems == null) {
	        cartItems = new ArrayList<>();
	    }

	    if (productQuantities == null) {
	        productQuantities = new HashMap<>();
	    }

	    if (product != null) {
	    	Integer currentQuantity = productQuantities.getOrDefault(productId, 0);
	        productQuantities.put(productId, currentQuantity + quantity);

	        if (!cartItems.contains(product)) {
	            cartItems.add(product);
	            productQuantities.put(productId, quantity);
	        }

	        session.setAttribute(SESSION_CART, cartItems);
	        session.setAttribute(SESSION_PRODUCT_QUANTITIES, productQuantities);
	    }

	    return "redirect:/front/index";
	}


	@DeleteMapping("/public/delete-product/{productId}")
	public ResponseEntity<?> deleteProduct(@PathVariable Integer productId, HttpSession session) {
	    List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
	    Map<Integer, Integer> productQuantities = (Map<Integer, Integer>) session.getAttribute(SESSION_PRODUCT_QUANTITIES);

	    if (cartItems != null && productQuantities != null) {
	       
	        Iterator<Product> iterator = cartItems.iterator();
	        while (iterator.hasNext()) {
	            Product product = iterator.next();
	            if (product.getProductId().equals(productId)) {
	                iterator.remove();
	                break;
	            }
	        }

	        productQuantities.remove(productId);	      
	        session.setAttribute(SESSION_CART, cartItems);
	        session.setAttribute(SESSION_PRODUCT_QUANTITIES, productQuantities);

	        return ResponseEntity.ok().build();
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}

}