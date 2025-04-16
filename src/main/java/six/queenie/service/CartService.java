package six.queenie.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecpay.logistics.integration.AllInOne;
import ecpay.logistics.integration.domain.CreateCVSObj;
import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.hsiao.service.MembersService;
import six.liang.model.AmountDiscount;
import six.liang.model.AmountDiscountRepository;
import six.liang.model.DiscountProductRepository;
import six.liang.model.ProductDiscount;
import six.pinhong.model.Product;
import six.pinhong.model.ProductRepository;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.model.OrdersRepository;
import six.yiting.model.StoresBean;

@Service
public class CartService {
	
	 private final AllInOne all;

	    @Autowired
	    public CartService() {
	        all = new AllInOne(""); 
	    }
    @Autowired
    private AmountDiscountRepository atRepository;

    @Autowired
    private DiscountProductRepository pdRepository;
    
    @Autowired
	private OrdersRepository ordersRepository;
	@Autowired
	private ProductRepository pRepository;
	
	@Autowired
	private OrderDetailService odservice;
	
	@Autowired
	private MembersRepository mRepository;
	@Autowired
	private MembersService mService;
	@Autowired
	private StoresService stService;
	@Autowired
	private OrderService oService;
   
	@Autowired
	private OrdersRepository oRepository;
	
	
    public Integer getAmountDiscount(Integer total, Integer amountDiscountId) {
    	if (amountDiscountId == null) {
	        return total; 
	    }
        Integer discountPercentage=0;
          
        Optional<AmountDiscount> mdOptional= atRepository.findById(amountDiscountId);
        if(mdOptional.isPresent()) {
        	AmountDiscount md = mdOptional.get();
         
        	discountPercentage = md.getDiscountPercentage();
        	
        } if (discountPercentage!= 0 ) {
            total=total- ((int)Math.round(total * discountPercentage / 100.0));           
        }else {
        	return total;
        }
        return total;
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
    public  Integer getMemberPoints(Integer memberId) {
		  
	 	   MembersBean md =mService.findByMemberId(memberId);	 	  
	 	   Integer mPoints = md.getPoints();
	 	    return mPoints;
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
        Integer newQuantity = 0;

        LocalDate currentDate = LocalDate.now();
        Date orderDate = Date.valueOf(currentDate);

        for (Product product : cartItems) {
            Integer productId = product.getProductId();
            Integer productPrice = product.getProductPrice();
            Integer quantity = productQuantities.getOrDefault(productId, 1);

            Integer productDiscountId = pdRepository.findProductDiscountId(productId, orderDate);
            Integer productDiscount = getProductDiscount(productDiscountId, orderDate);

            Integer subTotal;
            if (productDiscountId != null && productDiscountId != 0) {
                double discountAmount = productPrice * productDiscount / 100.0 * quantity;
                subTotal = productPrice * quantity - (int) Math.round(discountAmount);
            } else {
                subTotal = productPrice * quantity;
            }

            total += subTotal;
            sumTotal += productPrice * quantity;
            newQuantity += quantity;
        }

        Integer amountDiscountId = atRepository.findAmountDiscountId(total, orderDate);
        Integer amountDiscount = getAmountDiscount(total, amountDiscountId);
        Integer discountMoney = sumTotal - amountDiscount;

        Map<String, Integer> cartDetails = new HashMap<>();
        cartDetails.put("total", total);
        cartDetails.put("sumTotal", sumTotal);
        cartDetails.put("discountMoney", discountMoney);
        cartDetails.put("newQuantity", newQuantity);

        return cartDetails;
    }

    public Orders insertOrderFromCart(List<Product> cartItems, Map<Integer, Integer> productQuantities, 
    		Integer memberId,Integer storeId,String paymentMethod,Integer pointUse,String status,Integer unpaidCount ) {
        
        LocalDate currentDate = LocalDate.now();
        Date orderDate = Date.valueOf(currentDate);
       
        MembersBean member = mService.findByMemberId(memberId);
        StoresBean store = stService.findByStoreId(storeId);
        List<OrderDetails> orderDetailsList = new LinkedList<>();
        Integer total = 0;
        Integer sumTotal = 0;

        for (Product product : cartItems) {
            Integer productId = product.getProductId();
            Integer quantity = productQuantities.get(productId);

            Integer productDiscountId = pdRepository.findProductDiscountId(productId, orderDate);
            Integer productPrice = product.getProductPrice();
            Integer productDiscount = getProductDiscount(productDiscountId, orderDate);

            Integer subTotal;
            if (productDiscountId != null && productDiscountId != 0) {
                double result = productPrice * productDiscount / 100.0 * quantity;
                subTotal = productPrice * quantity - (int) Math.round(result);
            } else {
                subTotal = productPrice * quantity;
            }
            total += subTotal;
            sumTotal += productPrice * quantity;

            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setProduct(product);
            orderDetails.setQuantity(quantity);
            orderDetails.setSubTotal(subTotal);
            orderDetailsList.add(orderDetails);
        }

        Integer amountDiscountId = atRepository.findAmountDiscountId(total, orderDate);
        Integer amountDiscount = getAmountDiscount(total, amountDiscountId);
        Integer discountMoney = sumTotal - amountDiscount;
        Integer points = getMemberPoints(memberId);
        Integer finalAmount;
        

        if (pointUse == 1 && points != null && points <= amountDiscount) {
            pointUse = points;
            finalAmount = amountDiscount - pointUse;
        } else {
            pointUse = 0;
            finalAmount = amountDiscount;
        }

        Integer pointGet = (int) (finalAmount * 0.10);
        if (member != null) {
            member.setPoints(points != null ? points - pointUse + pointGet : pointGet);
        }

        Orders insertBean = new Orders();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(orderDate);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date orderSuccessDate = new Date(calendar.getTimeInMillis());
        calendar.setTime(orderDate);
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        Date pickupDate = new Date(calendar.getTimeInMillis());

        insertBean.setMembers(member);
        insertBean.setStoresBean(store);
        insertBean.setOrderDate(orderDate);
        insertBean.setPointUse(pointUse);
        insertBean.setPaymentMethod(paymentMethod);
        insertBean.setUnpaidCount(unpaidCount);
        insertBean.setTotal(sumTotal);
        insertBean.setDiscountMoney(discountMoney);
        insertBean.setFinalAmount(finalAmount);
        insertBean.setStatus(status);
        insertBean.setPointGet(pointGet);
        insertBean.setOrderSuccessDate(orderSuccessDate);
        insertBean.setPickupDate(pickupDate);

        Orders insertedOrder = ordersRepository.save(insertBean);
        
        for (OrderDetails orderDetails : orderDetailsList) {
            orderDetails.setOrders(insertedOrder);
            Product product = orderDetails.getProduct();
            product.setProductQuantity(product.getProductQuantity() - orderDetails.getQuantity());
            pRepository.save(product);
        }
        odservice.insertOrderDetails(orderDetailsList, insertedOrder);

        return insertedOrder;
    }

    

    public List<Orders> getOrdersByMember(MembersBean member) {
        return ordersRepository.findByMembers(member);
    }
    
    
    public void updateAccountStatus(Integer memberId, String lockStatus) {
    	MembersBean member = mService.findByMemberId(memberId);
        if (member != null) {          
            member.setLockStatus(lockStatus);
            mRepository.save(member);
        } else {
            throw new RuntimeException("會員未找到");
        }
    }
    //修改訂單狀態
    public void updateOrderStatus(Integer orderId,String status) {
    	
		Orders order = oService.getOrderById(orderId);
		if(order !=null) {
			order.setStatus(status);
			ordersRepository.save(order);
		}else {
            throw new RuntimeException("未找到訂單編號：" + orderId);
        }
	}
    public boolean processCheckout(Integer orderId, String paymentMethod) {
        if ("信用卡".equals(paymentMethod)) {
            updateOrderStatus(orderId, "訂單成立已付款");
        } else if ("現金支付".equals(paymentMethod)) {
           updateOrderStatus(orderId, "訂單成立未付款");
        } else {
        	 updateOrderStatus(orderId, "訂單不成立");
        }
		return false;
    }
    public  Map<String, List<Orders>> getOrdersGroupedByStatus(){
    	Map<String, List<Orders>> ordersByStatus = new HashMap<>();
      
        List<Orders> allOrders = ordersRepository.findAll();
        
        for (Orders order : allOrders) {
            String status = order.getStatus();
            String mappedStatus = mapStatus(status);
            //沒有值創建一個新的值插入
            ordersByStatus.computeIfAbsent(mappedStatus, k -> new ArrayList<>()).add(order);
        }
        
        return ordersByStatus;
    }
		
    public String mapStatus(String status) {
        switch (status) {
        case "訂單成立已付款":
            return "待出貨";
            case "訂單成立未付款":
                return "待出貨";           
            case "待發貨":
                return "待出貨";
            case "已送達":
                return "待收貨";
            case "運送中":
                return "待收貨";
            case "已出貨":
                return "待收貨";
            case "完成訂單":
                return "訂單已完成";
            case "訂單不成立":
                return "不成立";
            case "Canceled":
            	return "不成立";
            default:
                return status;
        }
    }
  
   
    public String createLogisticsOrder(Orders order, MembersBean loggedInMember) {
        Set<OrderDetails> itemDetails = order.getDetails();
        String itemNames = itemDetails.stream()
                .map(orderDetail -> orderDetail.getProduct().getProductName())
                .collect(Collectors.joining(", "));
        String merchantTradeNo = "202409" + order.getOrderId();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String currentTime = now.format(formatter);
        
        AllInOne all = new AllInOne("");

        CreateCVSObj obj = new CreateCVSObj();
        obj.setMerchantID("2000024110018");
        obj.setMerchantTradeNo(merchantTradeNo); 
        obj.setMerchantTradeDate(currentTime); 
        obj.setLogisticsSubType("FAMI");
        obj.setGoodsAmount(order.getFinalAmount().toString());
        obj.setGoodsName(itemNames); 
        obj.setSenderName("EZBuy購物");
        obj.setSenderPhone("0912345678");
        obj.setReceiverName(loggedInMember.getMemberName());
        obj.setReceiverPhone("0912345678");
        obj.setReceiverCellPhone("0912345678");
        obj.setReceiverEmail(loggedInMember.getMemberEmail());
        obj.setReceiverStoreID("006598");
        obj.setServerReplyURL("https://1e67-61-222-34-1.ngrok-free.app/FinalProject/return-logistics-status");

        String response = all.create(obj);
        String logisticsId = extractAllPayLogisticsID(response);
        String rtnMsg = extractRtnMsg(response);
        order.setLogisticsId(logisticsId);
        order.setLogisticStatus(rtnMsg);
        oRepository.save(order); 
        return logisticsId;
    }
    private String extractAllPayLogisticsID(String response) {
        String prefix = "AllPayLogisticsID=";
        int startIndex = response.indexOf(prefix);
        if (startIndex != -1) {
            int endIndex = response.indexOf('&', startIndex);
            if (endIndex == -1) {
                endIndex = response.length();
            }
            return response.substring(startIndex + prefix.length(), endIndex);
        }
        return null;
    }

    public String extractRtnMsg(String response) {
        String prefix = "RtnMsg=";
        int startIndex = response.indexOf(prefix);
        if (startIndex != -1) {
            int endIndex = response.indexOf('&', startIndex);
            if (endIndex == -1) {
                endIndex = response.length();
            }
            return response.substring(startIndex + prefix.length(), endIndex);
        }
        return null;
    }

}
   


