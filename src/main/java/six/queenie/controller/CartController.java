package six.queenie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.hsiao.service.EmailService;
import six.hsiao.service.MembersService;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.service.CartService;
import six.queenie.service.OrderDetailService;
import six.queenie.service.OrderService;
import six.yiting.model.StoresBean;
import six.yiting.service.StoreService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private EmailService emailService;
	@Autowired
	private OrderDetailService odService;
	@Autowired
	private MembersService mService;

	
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
        model.addAttribute("quantity", cartDetails.get("quantity"));

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
        	    Object productIdObj = item.get("productId");
        	    Object quantityObj = item.get("quantity");
        	    
        	    Integer productId = null;
        	    Integer newQuantity = null;
        	    
        	    if (productIdObj instanceof String) {
        	        try {
        	            productId = Integer.parseInt((String) productIdObj);
        	        } catch (Exception e) {
        	            e.printStackTrace();
        	        }
        	    } else if (productIdObj instanceof Integer) {
        	        productId = (Integer) productIdObj;
        	    }
        	    
        	    if (quantityObj instanceof String) {
        	        try {
        	            newQuantity = Integer.parseInt((String) quantityObj);
        	        } catch (NumberFormatException e) {
        	            e.printStackTrace();
        	        }
        	    } else if (quantityObj instanceof Integer) {
        	        newQuantity = (Integer) quantityObj;
        	    }
        	    
        	    if (productId != null && newQuantity != null) {
        	        productQuantities.put(productId, newQuantity);
        	    }
        	}
        }

        Integer totalAmount = (Integer) requestData.get("totalAmount");
        Integer discountAmount = (Integer) requestData.get("discountAmount");
        Integer finalAmount = (Integer) requestData.get("finalAmount");

        session.setAttribute(SESSION_CART, cartItems);
        session.setAttribute(SESSION_PRODUCT_QUANTITIES, productQuantities);

        Map<String, Object> response = new HashMap<>();
        response.put("totalAmount", totalAmount);
        response.put("discountAmount", discountAmount);
        response.put("finalAmount", finalAmount);
        response.put("productQuantities", productQuantities);
        response.put("updatedCartItems", updatedCartItems);

        return ResponseEntity.ok(response);
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

	    if (member == null) {
	        return "redirect:/public/frontLoginMain";
	    }

	    List<Orders> orders = cartService.getOrdersByMember(member);
	    if (orders == null || orders.isEmpty()) {
	        orders = new ArrayList<>();
	        Orders placeholderOrder = new Orders();
	        placeholderOrder.setMembers(member);
	        placeholderOrder.setStoresBean(new StoresBean()); 
	        orders.add(placeholderOrder);
	    }

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
	public String insertOrder(Model model, HttpSession session,
	                          @RequestParam(required = false) Integer memberId,
	                          @RequestParam String paymentMethod,
	                          @RequestParam Integer storeId,
	                          @RequestParam(required = false,defaultValue = "0") Integer pointUse,
	                          @RequestParam(required = false, defaultValue = "0") Integer unpaidCount, 
	                          @RequestParam(required = false) String status) {
	    List<Product> cartItems = (List<Product>) session.getAttribute(SESSION_CART);
	    Map<Integer, Integer> productQuantities = (Map<Integer, Integer>) session.getAttribute(SESSION_PRODUCT_QUANTITIES);
	    MembersBean loggedInMember = (MembersBean) session.getAttribute("loggedInMember");

	    
	    if (storeId == null) {	      
	    	model.addAttribute("errorMessage", "請選擇取貨店鋪!");
	    	 return "front/queenie/checkOut";
	    }
	    memberId = loggedInMember.getMemberId();

	    Orders orders = cartService.insertOrderFromCart(cartItems, productQuantities, memberId,
	            storeId, paymentMethod, pointUse, status, unpaidCount);
	    
	    if ("現金支付".equals(paymentMethod)) {
	    	 cartService.createLogisticsOrder(orders, loggedInMember);
	    }	
	    
	    MembersBean updatedMember = mService.findByMemberId(loggedInMember.getMemberId());
	    session.setAttribute("loggedInMember", updatedMember);
	    // 更新訂單狀態
	    cartService.processCheckout(orders.getOrderId(), paymentMethod);
	    // 發送訂單成功信件
	    emailService.sendOrderEmail(orders.getOrderId());
	    
	    if ("信用卡".equals(paymentMethod)) {
	    	cartService.createLogisticsOrder(orders, loggedInMember);
	        session.setAttribute(SESSION_CART, new ArrayList<>());
	        session.setAttribute(SESSION_PRODUCT_QUANTITIES, new HashMap<>());
	        return "redirect:/MemberOrdercheckout?id=" + orders.getOrderId();
	    }	  
	    List<Orders> ordersList = cartService.getOrdersByMember(loggedInMember);
	   
	    Map<String, List<Orders>> ordersByStatus = ordersList.stream()
	            .filter(order -> order.getStatus() != null)
	            .collect(Collectors.groupingBy(order -> cartService.mapStatus(order.getStatus())));

	    model.addAttribute("ordersByStatus", ordersByStatus);
	    model.addAttribute("orders", orders);
	    session.setAttribute(SESSION_CART, new ArrayList<>());
	    session.setAttribute(SESSION_PRODUCT_QUANTITIES, new HashMap<>());

	    return "/front/queenie/memberOrder";
	}


	@GetMapping("/public/MemberOrder")
	public String getAllOrders(HttpSession session, Model model) {

	    MembersBean loggedInMember = (MembersBean) session.getAttribute("loggedInMember");

	    if (loggedInMember == null) {
	        return "redirect:/public/frontLoginMain";
	    }

	    List<Orders> ordersList = cartService.getOrdersByMember(loggedInMember);
	    System.out.println(ordersList);

	    Map<String, List<Orders>> ordersByStatus = ordersList.stream()
	            .filter(order -> order.getStatus() != null)
	            .collect(Collectors.groupingBy(order -> cartService.mapStatus(order.getStatus())));

	    model.addAttribute("ordersByStatus", ordersByStatus);
	    model.addAttribute("ordersList", ordersList);
	    return "front/queenie/memberOrder";
	}

	@PostMapping("/public/CanceleOrder")
	public ResponseEntity<String> deleteOrder(@RequestBody Map<String, Integer> request, HttpSession session) {
	    Integer orderId = request.get("orderId");
	    MembersBean loggedInMember = (MembersBean) session.getAttribute("loggedInMember");
	    orderService.updateOrderStatusAndPoints(orderId, "Canceled");
	    MembersBean updatedMember = mService.findByMemberId(loggedInMember.getMemberId());
	    session.setAttribute("loggedInMember", updatedMember);
	    return ResponseEntity.ok("成功取消訂單");
	}

	@PostMapping("/public/CompleteOrder")
	public ResponseEntity<String> completeOrder(@RequestBody Map<String, Integer> request, HttpSession session) {
	    Integer orderId = request.get("orderId");
	    orderService.updateOrderStatus(orderId, "完成訂單");
	    
	    return ResponseEntity.ok("Order Complete successfully");
	}
	
	
	 @GetMapping("/public/MemberOrderDetail")
	    public String getDetailsById(@RequestParam("orderId") Integer orderId, Model model) {

	        List<OrderDetails> orderDetailsList = odService.getOrderDetailsByOrderId(orderId);
	        model.addAttribute("orderDetailsList", orderDetailsList);

	        return "front/queenie/memberOrderDetail";
	    }
}
	
