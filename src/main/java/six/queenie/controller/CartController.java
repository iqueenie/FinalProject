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
    private static final String SESSION_PRODUCT_QUANTITIES = "productQuantities";
    
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

        
        model.addAttribute("finalAmount", session.getAttribute("finalAmount"));

        Map<Integer, Integer> productQuantities = (Map<Integer, Integer>) session.getAttribute(SESSION_PRODUCT_QUANTITIES);
        if (productQuantities == null) {
            productQuantities = new HashMap<>();
            for (Product product : cartItems) {
                productQuantities.put(product.getProductId(), 1);
            }
            session.setAttribute(SESSION_PRODUCT_QUANTITIES, productQuantities);
        }

        cartService.calculateCartDiscounts(cartItems, new ArrayList<>(productQuantities.values()), session, model);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("productQuantities", productQuantities);
        model.addAttribute("total", session.getAttribute("total"));
        model.addAttribute("totalDiscount", session.getAttribute("totalDiscount"));
        model.addAttribute("productDiscountMap", session.getAttribute("productDiscountMap"));
        

        return "front/queenie/cart";
    }


    @PostMapping("/update-cart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(@RequestBody Map<String, Object> payload, HttpSession session) {
        List<Map<String, Object>> cartItemsData = (List<Map<String, Object>>) payload.get("cartItems");
        Integer totalAmount = 0;
        Object totalAmountObj = payload.get("totalAmount");
        if (totalAmountObj != null) {
            totalAmount = Integer.valueOf(totalAmountObj.toString());
        }

        List<Product> updatedCartItems = new ArrayList<>();
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
            }
        }
        session.setAttribute(SESSION_CART, updatedCartItems);

        cartService.calculateCartDiscounts(updatedCartItems, new ArrayList<>(), session, null);
        Integer discountAmount = (Integer) session.getAttribute("totalDiscount");
        Integer finalAmount = totalAmount - discountAmount;

        Map<String, Object> response = new HashMap<>();
        response.put("discountAmount", discountAmount);
        response.put("finalAmount", finalAmount);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/updateCartQuantities")
    public String updateCartQuantities(@RequestParam Map<String, String> quantities, HttpSession session, RedirectAttributes redirectAttributes) {
        List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute(SESSION_CART, cartItems);
        }

        Map<Integer, Integer> productQuantities = new HashMap<>();
        for (Map.Entry<String, String> entry : quantities.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("quantities[")) {
                Integer productId = Integer.valueOf(key.substring(11, key.length() - 1));
                Integer quantity = Integer.valueOf(entry.getValue());
                productQuantities.put(productId, quantity);
            }
        }

        session.setAttribute(SESSION_PRODUCT_QUANTITIES, productQuantities);

        cartService.calculateCartDiscounts(cartItems, new ArrayList<>(productQuantities.values()), session, redirectAttributes);

        Integer discountAmount = (Integer) session.getAttribute("totalDiscount");
        redirectAttributes.addFlashAttribute("discountAmount", discountAmount);

        return "redirect:/FinalProject/GetAllCart";
    }

    @GetMapping("/product/{productId}")
    public String getProductPage(@PathVariable("productId") Integer productId, Model model) {
        Product product = productService.findProductById(productId);
        model.addAttribute("product", product);
        return "/front/pinghong/AllProductPage";
    }

    @PostMapping("/addToCart")
    @ResponseBody
    public String addToCart(@RequestParam("productId") Integer productId, HttpSession session) {
        List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute(SESSION_CART, cartItems);
        }

        Product product = productService.findProductById(productId);
        if (product != null) {
            Map<Integer, Integer> productQuantities = (Map<Integer, Integer>) session.getAttribute(SESSION_PRODUCT_QUANTITIES);
            if (productQuantities == null) {
                productQuantities = new HashMap<>();
            }

            productQuantities.put(productId, 1);
            cartItems.add(product);
            session.setAttribute(SESSION_CART, cartItems);
            session.setAttribute(SESSION_PRODUCT_QUANTITIES, productQuantities);

            cartService.calculateCartDiscounts(cartItems, new ArrayList<>(productQuantities.values()), session, null);
        }
        return "redirect:/FinalProject/GetAllCart";
    }



    @GetMapping("/clearCart")
    public String clearCart(HttpSession session) {
        session.removeAttribute(SESSION_CART);
        session.removeAttribute(SESSION_PRODUCT_QUANTITIES); 
        return "redirect:/GetAllCart";
    }


    @GetMapping("/removeFromCart/{productId}")
    public String removeFromCart(@PathVariable("productId") Integer productId, HttpSession session, RedirectAttributes redirectAttributes) {
        List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute(SESSION_CART, cartItems);
        }

        cartItems.removeIf(product -> product.getProductId().equals(productId));
        cartService.calculateCartDiscounts(cartItems, new ArrayList<>(), session, null); 

        redirectAttributes.addFlashAttribute("message", "商品已從購物車移除");
        return "redirect:/GetAllCart/";
    }
}
