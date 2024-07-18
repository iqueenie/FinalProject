package six.liang.controller;

import java.util.HashMap;
import java.util.Map;

public class DiscountCategoryMapper {
    private static final Map<String, String> discountCategoryMap = new HashMap<>();

    static {
        discountCategoryMap.put("飲品優惠", "飲品");
        discountCategoryMap.put("零食優惠", "零食");
        discountCategoryMap.put("泡麵優惠", "泡麵");
        discountCategoryMap.put("熟食優惠", "熟食");
        discountCategoryMap.put("菸酒優惠", "菸酒");
        discountCategoryMap.put("生活用品優惠", "生活用品");
        discountCategoryMap.put("護理用品優惠", "護理用品");
        discountCategoryMap.put("夏季狂歡", "special");
        discountCategoryMap.put("週年慶", "special");
        discountCategoryMap.put("父親節", "special");
    }

    public static String getProductType(String discountName) {
        return discountCategoryMap.get(discountName);
    }
}
