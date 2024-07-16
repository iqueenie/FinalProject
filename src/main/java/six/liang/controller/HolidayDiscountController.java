package six.liang.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import six.liang.model.AmountDiscount;
import six.liang.model.HolidayDiscount;
import six.liang.service.AmountDiscountService;
import six.liang.service.HolidayDiscountService;

@Controller
public class HolidayDiscountController {

    @Autowired
    private HolidayDiscountService holidayDiscountService;

    @Autowired
    private AmountDiscountService amountDiscountService;

    private static final Logger logger = LoggerFactory.getLogger(HolidayDiscountController.class);

    // 查全部
    @GetMapping("/private/GetAllHolidayDiscounts")
    public String getAllHolidayDiscounts(Model model) {
        List<HolidayDiscount> holidayDiscounts = holidayDiscountService.findAll();
        List<String> discountImagesBase64 = holidayDiscounts.stream()
                .map(discount -> holidayDiscountService.getDiscountImageBase64(discount.getDiscountImage()))
                .collect(Collectors.toList());

        model.addAttribute("holidayDiscounts", holidayDiscounts);
        model.addAttribute("discountImagesBase64", discountImagesBase64);
        return "back/liang/GetAllHolidayDiscounts";
    }

    // 新增
    @GetMapping("/private/HolidayInsert")
    public String showHolidayInsertForm() {
        return "back/liang/InsertHoliday";
    }

    @PostMapping("/private/HolidayInsert")
    public String holidayInsert(@RequestParam("discountName") String discountName,
                                @RequestParam("discountInfo") String discountInfo,
                                @RequestParam("isActive") Integer isActive,
                                @RequestParam("discountImage") MultipartFile discountImage) throws IOException {

        HolidayDiscount discount = new HolidayDiscount();
        discount.setDiscountName(discountName);
        discount.setDiscountInfo(discountInfo);
        discount.setIsActive(isActive);

        if (!discountImage.isEmpty()) {
            discount.setDiscountImage(discountImage.getBytes());
        }

        holidayDiscountService.insert(discount);
        return "redirect:/private/GetAllHolidayDiscounts";
    }

    // 刪除
    @DeleteMapping("/private/holidayDiscount/delete")
    public ResponseEntity<String> deleteHolidayDiscount(@RequestParam Integer id) {
        try {
            holidayDiscountService.deleteById(id);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
        }
    }

    // 更新
    @GetMapping("/private/HolidayUpdate")
    public String showHolidayUpdateForm(@RequestParam("id") Integer id, Model model) {
        HolidayDiscount discount = holidayDiscountService.findById(id);

        // 處理圖片編碼
        if (discount.getDiscountImage() != null) {
            String encodedImage = Base64.getEncoder().encodeToString(discount.getDiscountImage());
            model.addAttribute("encodedImage", encodedImage);
        } else {
            model.addAttribute("encodedImage", null);
        }

        model.addAttribute("discount", discount);
        return "back/liang/UpdateHoliday";
    }

    @PostMapping("/private/HolidayUpdate")
    public String holidayUpdate(@RequestParam("id") Integer id,
                                @RequestParam("discountName") String discountName,
                                @RequestParam("discountInfo") String discountInfo,
                                @RequestParam("isActive") Integer isActive,
                                @RequestParam("discountImage") MultipartFile discountImage) throws IOException {

        HolidayDiscount discount = holidayDiscountService.findById(id);
        discount.setDiscountName(discountName);
        discount.setDiscountInfo(discountInfo);
        discount.setIsActive(isActive);

        if (!discountImage.isEmpty()) {
            discount.setDiscountImage(discountImage.getBytes());
        }

        holidayDiscountService.update(discount);
        return "redirect:/private/GetAllHolidayDiscounts";
    }

    // 前台
    @GetMapping("/public/front/Discount")
    public String Discounts(@RequestParam(value = "category", required = false) String category,
                            @RequestParam(value = "searchTerm", required = false) String searchTerm,
                            Model model) {
        List<HolidayDiscount> holidayDiscounts;
        List<AmountDiscount> amountDiscounts;

        if (category != null && category.equals("節日優惠")) {
            holidayDiscounts = List.of(); 
            amountDiscounts = amountDiscountService.findCurrentDiscounts();
        } else if (category != null && category.equals("特定商品優惠")) {
            holidayDiscounts = holidayDiscountService.findCurrentDiscounts();
            amountDiscounts = List.of(); 
        } else if (searchTerm != null && !searchTerm.isEmpty()) {
            holidayDiscounts = holidayDiscountService.findBySearchTermAndIsActive(searchTerm);
            amountDiscounts = amountDiscountService.findBySearchTermAndIsActive(searchTerm);
        } else {
            holidayDiscounts = holidayDiscountService.findCurrentDiscounts();
            amountDiscounts = amountDiscountService.findCurrentDiscounts();
        }

        List<String> holidayDiscountImagesBase64 = holidayDiscounts.stream()
                .map(discount -> holidayDiscountService.getDiscountImageBase64(discount.getDiscountImage()))
                .collect(Collectors.toList());

        List<String> amountDiscountImagesBase64 = amountDiscounts.stream()
                .map(discount -> amountDiscountService.getDiscountImageBase64(discount.getDiscountImage()))
                .collect(Collectors.toList());

        model.addAttribute("holidayDiscounts", holidayDiscounts);
        model.addAttribute("amountDiscounts", amountDiscounts);
        model.addAttribute("holidayDiscountImagesBase64", holidayDiscountImagesBase64);
        model.addAttribute("amountDiscountImagesBase64", amountDiscountImagesBase64);
        model.addAttribute("category", category);
        model.addAttribute("searchTerm", searchTerm);
        return "front/liang/Discount";
    }

    @GetMapping("/public/front/Discount/{name}")
    public String getDiscountByName(@PathVariable("name") String name, RedirectAttributes redirectAttributes) {
        Logger logger = LoggerFactory.getLogger(HolidayDiscountController.class);

        // 根據名稱獲取優惠訊息，並確定商品類別
        HolidayDiscount discount = holidayDiscountService.findByDiscountName(name);
        if (discount != null) {

            String productType = DiscountCategoryMapper.getProductType(discount.getDiscountName());

            if (productType != null) {
                logger.info("Product Type: {}", productType);
                redirectAttributes.addAttribute("productType", productType);
                return "redirect:/public/Products";
            } else {
                logger.info("No mapping found for Discount Name: {}", discount.getDiscountName());
            }
        } else {
            logger.info("Discount not found for name: {}", name);
        }
        // 其他返回活動頁面
        return "redirect:/public/front/Discount";
    }
}