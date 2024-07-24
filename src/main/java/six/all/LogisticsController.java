package six.all;

import org.springframework.web.bind.annotation.*;
import ecpay.logistics.integration.AllInOne;
import ecpay.logistics.integration.domain.CreateCVSObj;
import ecpay.logistics.integration.domain.QueryLogisticsTradeInfoObj;
import ecpay.logistics.integration.domain.UpdateStoreInfoObj;
import ecpay.logistics.integration.exception.EcpayException;
import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.service.CacheService;
import six.queenie.service.OrderService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class LogisticsController {

    private final AllInOne all;

    public LogisticsController() {
        all = new AllInOne(""); 
    }

    @Autowired
    private CacheService cacheService;
    @Autowired
    private OrderService oService;
    @Autowired
    private MembersService mService;
    
    @PostMapping("/selectStore")
    public ResponseEntity<String> selectStore(
            @RequestParam String storeId,
            @RequestParam String allPayLogisticsID,
            @RequestParam String cvsPaymentNo,
            @RequestParam String cvsValidationNo,
            @RequestParam Integer orderId) {

        if (storeId == null || storeId.isEmpty()) {
            return ResponseEntity.badRequest().body("店铺ID不能為空");
        }

        UpdateStoreInfoObj updateStoreInfoObj = new UpdateStoreInfoObj();
        updateStoreInfoObj.setAllPayLogisticsID(allPayLogisticsID);
        updateStoreInfoObj.setCVSPaymentNo(cvsPaymentNo);
        updateStoreInfoObj.setCVSValidationNo(cvsValidationNo);
        updateStoreInfoObj.setReceiverStoreID(storeId);
        updateStoreInfoObj.setStoreType("01");

        String result;
        try {
            result = all.updateStoreInfo(updateStoreInfoObj);          
           
        } catch (EcpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("更新店铺信息失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("更新店铺信息失败: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/public/logistics-status")
    public ResponseEntity<String> getLogisticsStatus(@RequestParam Integer orderId) {
        String allPayLogisticsID = cacheService.get(orderId);
        if (allPayLogisticsID == null) {
            return ResponseEntity.badRequest().body("物流单号不存在");
        }

        QueryLogisticsTradeInfoObj queryObj = new QueryLogisticsTradeInfoObj();
        queryObj.setAllPayLogisticsID("2837061");
        queryObj.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
        queryObj.setPlatformID("");

        String logisticsStatus;
        try {
            logisticsStatus = all.queryLogisticsTradeInfo(queryObj);
        } catch (EcpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("查询物流状态失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("查询物流状态失败: " + e.getMessage());
        }

        return ResponseEntity.ok(logisticsStatus);
    }





    

}
