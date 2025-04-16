package six.queenie.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import six.queenie.model.OrderDetails;
import six.queenie.service.OrderDetailService;

@Controller
public class OrderDetailController {
    @Autowired
    OrderDetailService oddService;

    @GetMapping("private/ShowDetail")
    public String getDetailsById(@RequestParam("orderId") int orderId, Model model) {

        List<OrderDetails> orderDetailsList = oddService.getOrderDetailsByOrderId(orderId);
        model.addAttribute("orderDetailsList", orderDetailsList);

        return "back/queenie/ShowDetail";
    }
}
