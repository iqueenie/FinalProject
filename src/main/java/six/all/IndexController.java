package six.all;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import six.hsiao.service.MembersService;
import six.queenie.service.OrderDetailService;

@Controller
public class IndexController {
	
	@Autowired
	private MembersService membersService;
	
	@Autowired
	private OrderDetailService orderDetailService;

	@ResponseBody
	@GetMapping("/GetMonthlyRegistrations")
	public List<Map<String, Object>> getMonthlyRegistrations() {
		
		List<Object[]> list = membersService.countRegistrationsPerMonth();
		
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] record : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", record[0]);
            map.put("month", record[1]);
            map.put("count", record[2]);
            result.add(map);
        }
		
		return result;
	}
	
	@ResponseBody
    @GetMapping("/GetMonthlySalesByProductType")
    public List<Object[]> getMonthlySalesByProductType() {
        return orderDetailService.getMonthlySalesByProductType();
    }
    
    @ResponseBody
    @GetMapping("/GetTotalSalesByProductType")
    public List<Object[]> getTotalSalesByProductType() {
        return orderDetailService.getTotalSalesByProductType();
    }
    
    @ResponseBody
    @GetMapping("/GetTop5BestSellingProducts")
    public List<Object[]> getTop5BestSellingProducts() {
        return orderDetailService.getTop5BestSellingProducts();
    }
}
