package six.liang.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import six.liang.model.AmountDiscount;
import six.liang.service.AmountDiscountService;

@Controller
public class AmountDiscountController {

    @Autowired
    private AmountDiscountService amountDiscountService;

    // 查全部
    @GetMapping("/GetAllAmountDiscount")
    public String getAllAmountDiscount(Model m) {
        List<AmountDiscount> amount = amountDiscountService.findAll();
        m.addAttribute("amount", amount);
        return "back/liang/GetAllAmountDiscounts";
    }

    // 新增
    @GetMapping("/AmountInserts")
    public String showAmountInsertForm() {
        return "back/liang/InsertAmount";
    }

    @PostMapping("/AmountInsert")
    public String amountInsert(@RequestParam("discountName") String discountName,
                               @RequestParam("minPurchaseAmount") Integer minPurchaseAmount,
                               @RequestParam("discountPercentage") Integer discountPercentage,
                               @RequestParam("startDate") Date startDate,
                               @RequestParam("endDate") Date endDate,
                               @RequestParam("isActive") Integer isActive) {

        AmountDiscount discount = new AmountDiscount();
        discount.setDiscountName(discountName);
        discount.setMinPurchaseAmount(minPurchaseAmount);
        discount.setDiscountPercentage(discountPercentage);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setIsActive(isActive);
        amountDiscountService.insert(discount);
        return "redirect:/GetAllAmountDiscount";
    }


    // 刪除
    @PostMapping("/AmountDelete")
    public String amountDelete(@RequestParam("id") Integer id) {
        amountDiscountService.deleteById(id);
        return "redirect:/GetAllAmountDiscount"; // 删除后重定向到列表页面
    }
}
