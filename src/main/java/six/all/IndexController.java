package six.all;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    private List<String> getLastTwelveMonths() {
        List<String> months = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 0; i < 12; i++) {
            LocalDate date = now.minusMonths(i);
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            months.add(0, formattedDate);  // 添加到列表的開頭
        }
        return months;
    }
	
	@ResponseBody
	@GetMapping("/private/GetMonthlyRegistrations")
	public ResponseEntity<Map<String, Object>> getMonthlyRegistrations() {
		
		List<Object[]> list = membersService.countRegistrationsPerMonth();
		
		List<String> labels = getLastTwelveMonths();
		
        List<Integer> counts = new ArrayList<>(Collections.nCopies(labels.size(), 0));

        for (Object[] record : list) {
            String formattedDate = String.format("%d-%02d", record[0], record[1]);
            int index = labels.indexOf(formattedDate);
            if (index != -1) {
                counts.set(index, ((Number) record[2]).intValue());
            }
        }
		
        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("counts", counts);

        return ResponseEntity.ok(response);
	}
	
	@ResponseBody
    @GetMapping("/private/GetMonthlySalesByProductType")
    public ResponseEntity<Map<String, Object>> getMonthlySalesByProductType() {
        List<Object[]> list = orderDetailService.getMonthlySalesByProductType();

        List<String> labels = getLastTwelveMonths();
        List<String> productTypes = Arrays.asList("飲品", "零食", "泡麵", "熟食");
        List<Map<String, Object>> datasets = new ArrayList<>();
        List<String> backgroundColors = Arrays.asList(
                "rgba(255, 99, 132, 0.2)",
                "rgba(54, 162, 235, 0.2)",
                "rgba(255, 206, 86, 0.2)",
                "rgba(75, 192, 192, 0.2)"
        );
        List<String> borderColors = Arrays.asList(
                "rgba(255, 99, 132, 1)",
                "rgba(54, 162, 235, 1)",
                "rgba(255, 206, 86, 1)",
                "rgba(75, 192, 192, 1)"
        );

        Map<String, List<Integer>> productTypeSalesMap = new HashMap<>();
        for (String productType : productTypes) {
            productTypeSalesMap.put(productType, new ArrayList<>(Collections.nCopies(labels.size(), 0)));
        }

        for (Object[] record : list) {
            String formattedDate = String.format("%d-%02d", record[0], record[1]);
            String productType = (String) record[2];
            int index = labels.indexOf(formattedDate);
            if (index != -1) {
                productTypeSalesMap.get(productType).set(index, ((Number) record[3]).intValue());
            }
        }

        for (int i = 0; i < productTypes.size(); i++) {
            String productType = productTypes.get(i);
            Map<String, Object> dataset = new HashMap<>();
            dataset.put("label", productType);
            dataset.put("data", productTypeSalesMap.get(productType));
            dataset.put("borderColor", borderColors.get(i));
            dataset.put("backgroundColor", backgroundColors.get(i));
            dataset.put("borderWidth", 1);
            datasets.add(dataset);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("datasets", datasets);

        return ResponseEntity.ok(response);
    }
    
    @ResponseBody
    @GetMapping("/private/GetTotalSalesByProductType")
    public ResponseEntity<Map<String, Object>> getTotalSalesByProductType() {
        List<Object[]> list = orderDetailService.getTotalSalesByProductType();

        List<String> productTypes = Arrays.asList("飲品", "零食", "泡麵", "熟食");
        List<Double> salesData = new ArrayList<>();
        List<String> backgroundColors = Arrays.asList(
            "rgba(255, 99, 132, 0.2)",
            "rgba(54, 162, 235, 0.2)",
            "rgba(255, 206, 86, 0.2)",
            "rgba(75, 192, 192, 0.2)"
        );
        List<String> borderColors = Arrays.asList(
            "rgba(255, 99, 132, 1)",
            "rgba(54, 162, 235, 1)",
            "rgba(255, 206, 86, 1)",
            "rgba(75, 192, 192, 1)"
        );

        // 初始化每個產品類型的銷售額列表
        Map<String, Double> productTypeSalesMap = new HashMap<>();
        for (String productType : productTypes) {
            productTypeSalesMap.put(productType, 0.0);
        }

        // 填充每個產品類型的銷售額數據
        for (Object[] record : list) {
            String productType = (String) record[0];
            if (productTypes.contains(productType)) {
                productTypeSalesMap.put(productType, ((Number) record[1]).doubleValue());
            }
        }

        // 構建銷售數據列表
        for (String productType : productTypes) {
            salesData.add(productTypeSalesMap.get(productType));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("labels", productTypes);
        response.put("salesData", salesData);
        response.put("backgroundColors", backgroundColors);
        response.put("borderColors", borderColors);
        
        return ResponseEntity.ok(response);
    }
    
    @ResponseBody
    @GetMapping("/private/GetTop5BestSellingProducts")
    public List<Object[]> getTop5BestSellingProducts() {
        return orderDetailService.getTop5BestSellingProducts();
    }
}
