package six.queenie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.liang.model.AmountDiscount;
import six.liang.model.AmountDiscountRepository;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.model.Orders;
import six.queenie.model.ProductDiscountRepository;
import six.queenie.service.CartService;
import six.queenie.service.OrderService;
import six.yiting.model.StoresBean;
import six.yiting.service.StoreService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private OrderService orderService;
    @Autowired
    private StoreService storeService;
    @Autowired
	private AmountDiscountRepository atRepository;
	@Autowired
	private ProductDiscountRepository pdRepository;
	
	
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
	
	@GetMapping("/public/CheckOut")
	public String checkOut(Model model, HttpSession session, HttpServletRequest request) {
	    List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
	    Map<Integer, Integer> productQuantities = (Map<Integer, Integer>) session.getAttribute(SESSION_PRODUCT_QUANTITIES);
	    MembersBean member = (MembersBean) session.getAttribute("loggedInMember"); 

	    Integer memberId = null;
	    if (member != null) {
	        memberId = member.getMemberId();
	    } else {
	    	return "redirect:/public/frontLoginMain";
	    }
	    List<Orders> orders = cartService.getOrdersByMember(member);
		
	    List<StoresBean> storeList = storeService.findAllStores();
	   

	    Map<String, Integer> cartDetails = cartService.calculateCartDetails(cartItems, productQuantities);

	    model.addAttribute("total", cartDetails.get("total"));
	    model.addAttribute("sumTotal", cartDetails.get("sumTotal"));
	    model.addAttribute("discountMoney", cartDetails.get("discountMoney"));
	    model.addAttribute("finalAmount", cartDetails.get("finalAmount"));
	    model.addAttribute("storeList", storeList);
	    model.addAttribute("orders", orders);
	    
	    return "front/queenie/checkOut";
	}

	@PostMapping("/public/insert")
	public String insertOrder(Model model, HttpServletRequest request, HttpSession session,
	                          @RequestParam(required = false) Integer memberId) {
	    List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
	    Map<Integer, Integer> productQuantities = (Map<Integer, Integer>) session.getAttribute(SESSION_PRODUCT_QUANTITIES);
	    MembersBean loggedInMember = (MembersBean) session.getAttribute("loggedInMember");

	    if (loggedInMember != null) {
	        memberId = loggedInMember.getMemberId();
	    } else if (memberId == null) {
	        String memberIdParam = request.getParameter("memberId");
	        if (memberIdParam != null && !memberIdParam.isEmpty()) {
	            memberId = Integer.parseInt(memberIdParam);
	        }
	    }
	    String paymentMethod = request.getParameter("paymentMethod");
	    Orders order = cartService.insertOrderFromCart(cartItems, productQuantities, memberId, request);	
	    
	    model.addAttribute("order", order);
	    session.setAttribute(SESSION_CART, new ArrayList<>());
	    session.setAttribute(SESSION_PRODUCT_QUANTITIES, new HashMap<>()); 

	    return "front/queenie/memberOrder";
	}
	
	@GetMapping("/public/MemberOrder")
	public String getAllOrders(HttpSession session, Model model) {

	    MembersBean loggedInMember = (MembersBean) session.getAttribute("loggedInMember");

	    if (loggedInMember == null) {
	        return "redirect:/public/frontLoginMain";
	    }

	    List<Orders> ordersList = cartService.getOrdersByMember(loggedInMember);

	    Map<String, List<Orders>> ordersByStatus = ordersList.stream()
	    		 .filter(order -> order.getStatus() != null)
	             .collect(Collectors.groupingBy(Orders::getStatus)); 

	    model.addAttribute("ordersByStatus", ordersByStatus);

	    return "front/queenie/memberOrder";
	}


}