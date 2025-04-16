package six.all;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.service.CartService;
import six.queenie.service.OrderService;
import six.sunny.model.GroupMember;
import six.sunny.service.GroupMemberService;

@Controller
public class ECPayController {

	private final AllInOne all;

	public ECPayController() {
		all = new AllInOne("");
	}

	@Autowired
	GroupMemberService groupMemberService;
	
	@Autowired
	private OrderService oService;
	@Autowired
	private CartService cartService;
	

	@ResponseBody
	@GetMapping("/group-buy-checkout")
	public String checkout(@RequestParam Integer id) {

		GroupMember groupMember = groupMemberService.findById(id);

		String merchantTradeNo = "TESTgroupbuyd" + id;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String currentTime = sdf.format(new Date());

		AllInOne all = new AllInOne("");

		AioCheckOutALL obj = new AioCheckOutALL();
		obj.setMerchantTradeNo(merchantTradeNo);
		obj.setMerchantTradeDate(currentTime);
		obj.setTotalAmount(groupMember.getTotal().toString());
		obj.setTradeDesc("團購商品付款");
		obj.setItemName(groupMember.getGroupBuy().getProductName());
		obj.setReturnURL("https://f124-61-222-34-1.ngrok-free.app/FinalProject/group-buy-checkout-return");
		obj.setClientBackURL("http://localhost:8080/FinalProject/public/front/group-member-orders");
		obj.setNeedExtraPaidInfo("N");

		String form = all.aioCheckOut(obj, null);
		return form;
	}

	@ResponseBody
	@PostMapping("/group-buy-checkout-return")
	public String handleReturn(@RequestParam Map<String, String> allParams) {

		boolean checkStatus = all.compareCheckMacValue(new Hashtable<>(allParams));

		if (checkStatus) {
			String merchantTradeNo = allParams.get("MerchantTradeNo");
			String rtnCode = allParams.get("RtnCode");

			String idString = merchantTradeNo.replace("TESTgroupbuyd", "");
			Integer id = Integer.parseInt(idString);

			System.out.println("驗證成功");
			
			if ("1".equals(rtnCode)) {
				groupMemberService.changePaymentStatus(id);
			}

			return "1|OK";
		} else {
			return "0|FAIL";
		}
	}
	
	@ResponseBody
	@GetMapping("/MemberOrdercheckout")
	public String ordercheckout(@RequestParam Integer id) {

		Orders order = oService.getOrderById(id);
		Set<OrderDetails> itemDetails = order.getDetails();
		String itemNames = itemDetails.stream()
		        .map(orderDetail -> orderDetail.getProduct().getProductName())
		        .collect(Collectors.joining(", "));
		String merchantTradeNo = "2024000110000" + id;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String currentTime = sdf.format(new Date());

		AllInOne all = new AllInOne("");

		AioCheckOutALL obj = new AioCheckOutALL();
		obj.setMerchantTradeNo(merchantTradeNo);
		obj.setMerchantTradeDate(currentTime);
		obj.setTotalAmount(order.getFinalAmount().toString());
		obj.setTradeDesc("會員商品付款");
		obj.setItemName(itemNames);
		obj.setReturnURL("https://f124-61-222-34-1.ngrok-free.app/FinalProject/order-checkout-return");
		obj.setClientBackURL("http://localhost:8080/FinalProject/public/MemberOrder");
		obj.setNeedExtraPaidInfo("N");

		String form = all.aioCheckOut(obj, null);
		return form;
	}
	
		@ResponseBody
	    @PostMapping("/order-checkout-return")
	    public String returnFromPaymentGateway(@RequestParam Map<String, String> allParams) {
	        boolean checkStatus = all.compareCheckMacValue(new Hashtable<>(allParams));

	        if (checkStatus) {
	            String merchantTradeNo = allParams.get("MerchantTradeNo");
	            String rtnCode = allParams.get("RtnCode");
	           

	            String idString = merchantTradeNo.replace("20240800", "");
	            Integer id = Integer.parseInt(idString);
	          

	            System.out.println("驗證成功");

	            if ("1".equals(rtnCode)) {
	                cartService.processCheckout(id, "信用卡");	                	               

	                return "redirect:/front/queenie/memberOrder";
	            }

	            return "1|OK";
	        } else {
	            return "0|FAIL";
	        }
	    }
	
	}

	

	

