package six.liang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import six.liang.service.DiscountEmailService;

@Controller
public class EmailController {

    @Autowired
    private DiscountEmailService emailService;

    @PostMapping("/FinalProject/admin/sendPromotionEmailForDiscount")
    public String sendPromotionEmailForDiscount(@RequestParam Integer discountId, Model model) {
        emailService.sendPromotionEmailForDiscount(discountId);
        model.addAttribute("message", "優惠訊息已發送至所有會員！");
        return "redirect:/private/GetAllAmountDiscount";
    }

    @PostMapping("/FinalProject/admin/sendPromotionEmail")
    public String sendPromotionEmailToAllMembers(Model model) {
        emailService.sendPromotionEmailToAllMembers();
        model.addAttribute("message", "優惠訊息已發送至所有會員！");
        return "back/liang/sendPromotionEmail";
    }
}
