package six.queenie.service;

import java.lang.reflect.Member;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.hsiao.service.MembersService;
import six.liang.model.AmountDiscount;
import six.liang.model.AmountDiscountRepository;
import six.liang.model.DiscountProductRepository;
import six.liang.model.ProductDiscount;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.model.OrdersRepository;
import six.yiting.model.StoresBean;


@Service
@Transactional
public class OrderService {
	
	@Autowired
	private OrdersRepository ordersRepository;
	@Autowired
	private AmountDiscountRepository atRepository;
	@Autowired
	private DiscountProductRepository pdRepository;
	@Autowired
	private ProductService pService;
	
	@Autowired
	private OrderDetailService odservice;
	
	@Autowired
	private MembersRepository mRepository;
	@Autowired
	private MembersService mService;
	@Autowired
	private StoresService stService;
	
	
	public List<Orders> findAll() {
		return ordersRepository.findAll();
		
	}

	 public Orders getOrderById(Integer orderId) {
	       
         return ordersRepository.findByOrderId(orderId);
     }
	
	 public void updateOrderStatusAndPoints(Integer orderId, String newStatus) {
		    Orders order = ordersRepository.findByOrderId(orderId);
		    if (order != null) {
		        order.setStatus(newStatus);
		        MembersBean member = order.getMembers();
		        
		        if (member != null) {
		            Integer memberPoints = member.getPoints();
		            Integer orderPoints = order.getPointGet();

		            if (memberPoints == null) {
		                memberPoints = 0;
		            }
		            if (orderPoints == null) {
		                orderPoints = 0;
		            }

		            int newMemberPoints = memberPoints - orderPoints;
		            if (newMemberPoints < 0) {
		                newMemberPoints = 0;
		            }
		            
		            member.setPoints(newMemberPoints);
		            mRepository.save(member);
		        }
		        
		        ordersRepository.save(order);
		    }
		}
	 
	 public void updateOrderStatus(Integer orderId, String newStatus) {
		    Orders order = ordersRepository.findByOrderId(orderId);
		    if (order != null) {
		        order.setStatus(newStatus);
		        ordersRepository.save(order);
		    }
		}
	 
	 
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
	    
	    public  Integer getMemberPoints(Integer memberId) {
	    		  
	 	   MembersBean md =mService.findByMemberId(memberId);	 	  
	 	   Integer mPoints = md.getPoints();
	 	    return mPoints;
	}
	    
	   
	    public Orders insertOrder(HttpServletRequest request) {
	        String[] productId = request.getParameterValues("productId[]");
	        String[] quantity = request.getParameterValues("quantity[]");
	        Integer memberId = Integer.parseInt(request.getParameter("memberId"));
	        Integer storeId = Integer.parseInt(request.getParameter("storeId"));
	      
	        Date orderDate = Date.valueOf(request.getParameter("orderDate"));
	        String paymentMethod = request.getParameter("paymentMethod");
	        Integer usePoints = Integer.parseInt(request.getParameter("pointUse"));
	        Integer unpaidCount = Integer.parseInt(request.getParameter("unpaidCount"));
	        String status = request.getParameter("status");

	        MembersBean member = mService.findByMemberId(memberId);
	        StoresBean store = stService.findByStoreId(storeId);
	        List<OrderDetails> orderDetailsList = new LinkedList<>();
	        Integer total = 0;
	        Integer sumTotal = 0;

	        for (int i = 0; i < productId.length; i++) {
	            Integer productDiscountId = pdRepository.findProductDiscountId(Integer.parseInt(productId[i]), orderDate);
	            Product product = pService.findProductById(Integer.parseInt(productId[i]));
	            Integer quant = Integer.parseInt(quantity[i]);	            
	            Integer productPrice = product.getProductPrice(); 
	            Integer productDiscount = getProductDiscount(productDiscountId);

	            Integer subTotal;
	            if (productDiscountId != null && productDiscountId != 0) {
	                double result = productPrice * productDiscount / 100.0 * quant;
	                subTotal = productPrice * quant - (int) Math.round(result);
	            } else {
	                subTotal = productPrice * quant;
	            }
	            total += subTotal;
	            sumTotal += productPrice * quant;

	            OrderDetails orderDetails = new OrderDetails();
	            orderDetails.setProduct(product);
	            orderDetails.setQuantity(quant);
	            orderDetails.setSubTotal(subTotal);
	            orderDetailsList.add(orderDetails);
	        }

	        Integer amountDiscountId = atRepository.findAmountDiscountId(total, orderDate);
	        Integer amountDiscount = getAmountDiscount(total, amountDiscountId);
	        Integer discountMoney = sumTotal - amountDiscount;
	        Integer points = getMemberPoints(memberId);
	        Integer finalAmount;
	        Integer pointUse;

	        if (usePoints == 1 && points != null && points <= amountDiscount) {
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
	        }
	        odservice.insertOrderDetails(orderDetailsList, insertedOrder);

	        return insertedOrder;
	    }
	    
	    public Orders updateOrder(Integer orderId, Date newOrderDate) {
	        Orders order = ordersRepository.findByOrderId(orderId);
	            order.setOrderDate(newOrderDate);
	            List<OrderDetails> orderDetailsList = odservice.getOrderDetailsByOrderId(orderId);
	            UpdateOrderAmounts(order, orderDetailsList, newOrderDate);
	            return ordersRepository.save(order);
	    
	    }

	    public void UpdateOrderAmounts(Orders order, List<OrderDetails> orderDetailsList, Date orderDate) {
	       
	        Integer sumTotal = 0;
	        Integer total = 0;

	        for (OrderDetails orderDetails : orderDetailsList) {
	            Product product = orderDetails.getProduct();
	            Integer productId = product.getProductId();
	            Integer productPrice = product.getProductPrice();
	            Integer quantity = orderDetails.getQuantity();

	            // 獲取產品折扣
	            Integer productDiscountId = pdRepository.findProductDiscountId(productId, orderDate);
	            Integer productDiscount = getProductDiscount(productDiscountId);

	            // 計算小計
	            Integer subTotal;
	            if (productDiscount != null && productDiscount > 0) {
	                double discountedAmount = productPrice * (100 - productDiscount) / 100.0;
	                subTotal = (int) Math.round(discountedAmount * quantity);
	            } else {
	                subTotal = productPrice * quantity;
	            }

	            // 更新訂單明細的小計
	            orderDetails.setSubTotal(subTotal);

	            // 累計訂單總金額
	            total += subTotal;
	            sumTotal += productPrice * quantity;
	        }
	        
	        // 獲取訂單折扣
	        Integer amountDiscountId = atRepository.findAmountDiscountId(total, orderDate);
	        Integer amountDiscount = getAmountDiscount(total, amountDiscountId);

	        // 更新會員積分
	        Integer memberId = order.getMembers().getMemberId();
	        Integer points = getMemberPoints(memberId);
	        Integer usePoints = order.getPointUse();
	        Integer finalAmount;

	        if (usePoints == 1 && points != null && points <= amountDiscount) {
	            Integer pointUse = points;
	            finalAmount = amountDiscount - pointUse;
	            order.getMembers().setPoints(points - pointUse + (int) (finalAmount * 0.10));
	        } else {
	            order.getMembers().setPoints((int) (amountDiscount * 0.10));
	            finalAmount = amountDiscount;
	        }
	        Integer pointGet = (int) (finalAmount * 0.10);
	        Integer discountMoney = sumTotal - amountDiscount;
	       
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(orderDate);
	        calendar.add(Calendar.DAY_OF_MONTH, 7);
	        Date orderSuccessDate = new Date(calendar.getTimeInMillis());
	        calendar.setTime(orderDate);
	        calendar.add(Calendar.DAY_OF_MONTH, 5);
	        Date pickupDate = new Date(calendar.getTimeInMillis());

	        // 更新訂單的總金額及折扣資訊
	        order.setTotal(sumTotal);
	        order.setDiscountMoney(discountMoney);
	        order.setFinalAmount(finalAmount);
	        order.setPointGet(pointGet);
	        order.setOrderSuccessDate(orderSuccessDate);
	        order.setPickupDate(pickupDate);
	    }


	}



