package six.all;

import org.springframework.web.bind.annotation.*;

import ecpay.logistics.integration.AllInOne;
import ecpay.logistics.integration.domain.QueryLogisticsTradeInfoObj;
import ecpay.logistics.integration.exception.EcpayException;
import six.hsiao.service.MembersService;
import six.queenie.model.Orders;
import six.queenie.model.OrdersRepository;
import six.queenie.service.CartService;
import six.queenie.service.OrderService;

import java.util.Hashtable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class LogisticsController {

    private final AllInOne all;

    public LogisticsController() {
        all = new AllInOne(""); 
    }

    @Autowired
    private OrderService oService;
    @Autowired
    private MembersService mService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrdersRepository oRepository;
    
    @GetMapping("/public/logistics-status")
    public String getLogisticsStatus(@RequestParam Integer orderId, @RequestParam Map<String, String> params, Model model) {
        Orders order = oService.getOrderById(orderId);
        if (order == null) {
            model.addAttribute("message", "訂單不存在");
            return "front/queenie/checkLogisticsStatus";
        }

        String allPayLogisticsID = order.getLogisticsId();
        String logisticStatus = order.getLogisticStatus();

        if (params.containsKey("CheckMacValue")) {
            long currentTime = System.currentTimeMillis() / 1000;
            String requestTimestampStr = params.get("TimeStamp");
            long requestTimestamp = Long.parseLong(requestTimestampStr);

            long timeInterval = 3 * 60; 
            if (Math.abs(currentTime - requestTimestamp) <= timeInterval) {
                boolean checkStatus = all.compareCheckMacValue(new Hashtable<>(params));
                
                if (checkStatus) {
                    QueryLogisticsTradeInfoObj queryObj = new QueryLogisticsTradeInfoObj();
                    queryObj.setMerchantID("2000132");
                    queryObj.setAllPayLogisticsID(allPayLogisticsID);
                    queryObj.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
                    queryObj.setPlatformID("");

                    String response = all.queryLogisticsTradeInfo(queryObj);
                    logisticStatus = cartService.extractRtnMsg(response);

                    order.setLogisticStatus(logisticStatus);
                    oRepository.save(order);
                }
            }
        }

        model.addAttribute("orderID", orderId);
        model.addAttribute("logisticsID", allPayLogisticsID);
        model.addAttribute("logisticStatus", logisticStatus);

        return "front/queenie/checkLogisticsStatus";
    }
   
    @GetMapping("/return-logistics-status")
    public String handleLogisticsStatus(@RequestParam Map<String, String> params) {
        

            boolean checkStatus = all.compareCheckMacValue(new Hashtable<>(params));
            if (checkStatus) {
            
            String merchantTradeNo = params.get("MerchantTradeNo");
            String rtnCode = params.get("RtnCode");

            String idString = merchantTradeNo.replace("20241211000", "");
            Integer id = Integer.parseInt(idString);

            System.out.println("驗證成功");

            if ("300".equals(rtnCode)) {
                cartService.processCheckout(id, "綠界接收訂單");

              

                return "redirect:/front/queenie/memberOrder";
            }

            return "1|OK";
        } else {
            return "0|FAIL";
        }
    }

        
}


