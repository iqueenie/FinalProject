package six.all;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import six.sunny.service.GroupMemberService;

@Controller
public class LinePayController {

	@Autowired
    private LinePayService linePayService;
	
	@Autowired
	private GroupMemberService groupMemberService;

	@GetMapping("/group-buy-line-pay")
	public String GroupBuyLinePay(@RequestParam Integer id) {
		JsonNode node = linePayService.sendPost(id);
		System.out.println(node);
        String webUrl = node.path("info")
                .path("paymentUrl")
                .path("web")
                .asText();
		
		return "redirect:"+webUrl;
	}
	
	@GetMapping("/group-buy-line-pay/confirm")
	public String confirmPayment(@RequestParam("transactionId") String transactionId,
	                             @RequestParam("orderId") String orderId) {
		
		System.out.println(transactionId + " " + orderId + "確認付款");
		
//	    boolean isPaymentSuccessful = linePayService.confirmTransaction(transactionId, orderId);
		
		
	    
//	    System.out.println("結果"+isPaymentSuccessful);
	    
//	    if (isPaymentSuccessful) {
	    	int id = Integer.parseInt(orderId.replace("TestGroupbuye", ""));
	        groupMemberService.changePaymentStatus(id);
//	    } 
	    
        return "redirect:/public/front/group-member-orders";
	}
	
	


}
