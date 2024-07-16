package six.liang.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import six.liang.model.AmountDiscount;
import six.liang.service.AmountDiscountService;

@Controller
public class AmountDiscountController {

    @Autowired
    private AmountDiscountService amountDiscountService;

    // 查全部
    @GetMapping("/private/GetAllAmountDiscount")
    public String getAllAmountDiscount(Model model) {
        List<AmountDiscount> amountDiscounts = amountDiscountService.findAll();
        List<String> discountImagesBase64 = amountDiscounts.stream()
                .map(discount -> amountDiscountService.getDiscountImageBase64(discount.getDiscountImage()))
                .collect(Collectors.toList());

        model.addAttribute("amountDiscounts", amountDiscounts);
        model.addAttribute("discountImagesBase64", discountImagesBase64);
        return "back/liang/GetAllAmountDiscounts";
    }


    // 新增
    @GetMapping("/private/AmountInserts")
    public String showAmountInsertForm() {
        return "back/liang/InsertAmount";
    }

    @PostMapping("/private/AmountInsert")
    public String amountInsert(@RequestParam("discountName") String discountName,
                               @RequestParam("minPurchaseAmount") Integer minPurchaseAmount,
                               @RequestParam("discountPercentage") Integer discountPercentage,
                               @RequestParam("startDate") Date startDate,
                               @RequestParam("endDate") Date endDate,
                               @RequestParam("isActive") Integer isActive,
                               @RequestParam("discountDescription") String discountDescription,
                               @RequestParam("discountImage") MultipartFile discountImage) throws IOException {

        
        AmountDiscount discount = new AmountDiscount();
        discount.setDiscountName(discountName);
        discount.setMinPurchaseAmount(minPurchaseAmount);
        discount.setDiscountPercentage(discountPercentage);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setIsActive(isActive);
        discount.setDiscountDescription(discountDescription);

        if (!discountImage.isEmpty()) {
            discount.setDiscountImage(discountImage.getBytes());
        }

        amountDiscountService.insert(discount);
        return "redirect:/private/GetAllAmountDiscount";
    }

    // 刪除
    @DeleteMapping("/private/amountDiscount/delete")
    public ResponseEntity<String> deleteAmountDiscount(@RequestParam Integer id) {
        System.out.println("Deleting discountId: " + id);
        try {
            amountDiscountService.deleteById(id);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
        }
    }

    // 更新
    @GetMapping("/private/AmountUpdates")
    public String showAmountUpdateForm(@RequestParam("discountId") Integer discountId, Model model) {
        AmountDiscount discount = amountDiscountService.findById(discountId);

        //處理圖片編碼
        if (discount.getDiscountImage() != null) {
            String encodedImage = Base64.getEncoder().encodeToString(discount.getDiscountImage());
            model.addAttribute("encodedImage", encodedImage);
        } else {
            model.addAttribute("encodedImage", null);
        }

        model.addAttribute("discount", discount);
        return "back/liang/UpdateAmount";
    }


    @PostMapping("/private/AmountUpdate")
    public String amountUpdate(@RequestParam("discountId") Integer discountId,
                               @RequestParam("discountName") String discountName,
                               @RequestParam("minPurchaseAmount") Integer minPurchaseAmount,
                               @RequestParam("discountPercentage") Integer discountPercentage,
                               @RequestParam("startDate") Date startDate,
                               @RequestParam("endDate") Date endDate,
                               @RequestParam("isActive") Integer isActive,
                               @RequestParam("discountDescription") String discountDescription,
                               @RequestParam("discountImage") MultipartFile discountImage) throws IOException {

        AmountDiscount discount = amountDiscountService.findById(discountId);
        discount.setDiscountName(discountName);
        discount.setMinPurchaseAmount(minPurchaseAmount);
        discount.setDiscountPercentage(discountPercentage);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setIsActive(isActive);
        discount.setDiscountDescription(discountDescription);

        if (!discountImage.isEmpty()) {
            discount.setDiscountImage(discountImage.getBytes());
        }

        amountDiscountService.update(discount);
        return "redirect:/private/GetAllAmountDiscount";
    }
    


}
