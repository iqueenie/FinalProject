package six.queenie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.service.CartService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    private static final String SESSION_CART = "sessionCart";
    
    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;


    @GetMapping("/GetAllCart")
    public String showCart(Model model, HttpSession session) {
        List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute(SESSION_CART, cartItems);
        }

        cartService.calculateCartDiscounts(cartItems, new ArrayList<>(), session, model);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", session.getAttribute("total"));
        model.addAttribute("totalDiscount", session.getAttribute("totalDiscount"));
        model.addAttribute("productDiscountMap", session.getAttribute("productDiscountMap"));
        model.addAttribute("finalAmount", session.getAttribute("finalAmount"));

        Map<Integer, Integer> productQuantities = new HashMap<>();
        for (Product product : cartItems) {
            productQuantities.put(product.getProductId(), 1); 
        }
        model.addAttribute("productQuantities", productQuantities);

        return "front/queenie/cart";
    }

    @PostMapping("/updateCart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(@RequestBody Map<String, Object> payload, HttpSession session) {
        List<Map<String, Object>> cartItemsData = (List<Map<String, Object>>) payload.get("cartItems");
        Integer totalAmount = 0;
        Object totalAmountObj = payload.get("totalAmount");
        if (totalAmountObj != null) {
            totalAmount = Integer.valueOf(totalAmountObj.toString());
        }

        List<Product> updatedCartItems = new ArrayList<>();
        Map<Integer, Integer> updatedQuantities = new HashMap<>();

        for (Map<String, Object> itemData : cartItemsData) {
            if (itemData.get("productId") == null || itemData.get("quantity") == null) {
                continue; 
            }

            Integer productId = Integer.valueOf(itemData.get("productId").toString());
            Integer quantity = Integer.valueOf(itemData.get("quantity").toString());

            Product product = productService.findProductById(productId);
            if (product != null) {
                for (int i = 0; i < quantity; i++) {
                    updatedCartItems.add(product);
                }
                updatedQuantities.put(productId, quantity);
            }
        }

        session.setAttribute(SESSION_CART, updatedCartItems);

        cartService.calculateCartDiscounts(updatedCartItems, new ArrayList<>(updatedQuantities.values()), session, null);
        Integer discountAmount = (Integer) session.getAttribute("totalDiscount");
        Integer finalAmount = totalAmount - discountAmount;

        Map<String, Object> response = new HashMap<>();
        response.put("discountAmount", discountAmount);
        response.put("finalAmount", finalAmount);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/product/{productId}")
    public String getProductPage(@PathVariable("productId") Integer productId, Model model) {
        Product product = productService.findProductById(productId);
        model.addAttribute("product", product);
        return "front/pinhong/AllProductPage";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("productId") Integer productId, HttpSession session, RedirectAttributes redirectAttributes) {
        List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            
            session.setAttribute(SESSION_CART, cartItems);
        }

        Product product = productService.findProductById(productId);
        if (product != null) {
            cartItems.add(product);
        }

        cartService.calculateCartDiscounts(cartItems, new ArrayList<>(), session, null); 
        
        redirectAttributes.addFlashAttribute("message", "商品添加成功");
        return "redirect:/product/" + productId;
    }
    
    @PostMapping("/deleteProduct")
    public String deleteProduct(@RequestParam("productId") Integer productId, HttpSession session, RedirectAttributes redirectAttributes) {
        List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
        if (cartItems != null) {
            Product productToDelete = cartItems.stream()
                    .filter(p -> p.getProductId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (productToDelete != null) {
                cartItems.remove(productToDelete);
                session.setAttribute(SESSION_CART, cartItems);

                cartService.calculateCartDiscounts(cartItems, new ArrayList<>(), session, null);

                redirectAttributes.addFlashAttribute("message", "商品删除成功");
            } else {
                redirectAttributes.addFlashAttribute("message", "找不到要删除的商品");
            }
        }

        return "redirect:/GetAllCart";
    }

}
