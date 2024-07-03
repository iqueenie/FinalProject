package six.queenie.service;

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
import six.liang.model.AmountDiscount;
import six.liang.model.ProductDiscount;
import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.queenie.model.AmountRepository;
import six.queenie.model.MembersRepository;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.service.StoresService;
import six.queenie.model.OrdersRepository;
import six.queenie.model.ProductDiscountRepository;
import six.yiting.model.StoresBean;


@Service
@Transactional
public class OrderService {
	
	@Autowired
	private OrdersRepository ordersRepository;
	@Autowired
	private AmountRepository atRepository;
	@Autowired
	private ProductDiscountRepository pdRepository;
	@Autowired
	private ProductService pService;
	
	@Autowired
	private OrderDetailService odservice;
	
	@Autowired
	private MembersRepository mRepository;
	@Autowired
	private StoresService stService;
	
	public List<Orders> findAll() {
		return ordersRepository.findAll();
		
	}

	 public Orders getOrderById(Integer orderId) {
	       
         return ordersRepository.findByOrderId(orderId);
     }
	 
	 public boolean updateOrderStatus(Integer orderId, String newStatus) {
     Orders order = ordersRepository.findByOrderId(orderId);
     if (order != null) {
         order.setStatus(newStatus);
         ordersRepository.save(order);
         return true;
     }
     return false;
 }
	 public Integer getAmountDiscount(Integer total, Integer amountDiscountId) {
		    
	        Integer discountPercentage=0;
	          
	        Optional<AmountDiscount> mdOptional= atRepository.findById(amountDiscountId);
	        if(mdOptional.isPresent()) {
	        	AmountDiscount md = mdOptional.get();
	         
	        	discountPercentage = md.getDiscountPercentage();
	        	
	        } if (discountPercentage!= 0 ) {
	        	 System.out.println("total1: "+ total);
	            total=total- ((int)Math.floor(total * discountPercentage / 100.0));           
	        }else {
	        	return total;
	        }
	        return total;
	    }
	    
	    public Integer getProductDiscount(Integer  productDiscountId) {
	        Integer discountPercentage=0;
	          
	        Optional<ProductDiscount> pdOptional = pdRepository.findById(productDiscountId);
	        if(pdOptional.isPresent()) {
	        	ProductDiscount pd = pdOptional.get();
	        	discountPercentage = pd.getDiscountPercentage();
       
	        }
	        return discountPercentage;
	    }
	    
	    public  Integer getMemberPoints(Integer memberId) {
	    		  
	 	    Optional<MembersBean> mdOptional =mRepository.findById(memberId);
	 	    MembersBean md = mdOptional.get();
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

	        MembersBean member = mRepository.findByMemberId(memberId);
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
	                subTotal = productPrice * quant - (int) Math.floor(result);
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

	    public Orders updateOrder(Orders order) {

	        return ordersRepository.save(order);
	    }
	}


