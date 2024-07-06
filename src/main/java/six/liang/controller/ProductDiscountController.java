package six.liang.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import six.liang.model.ProductDiscount;
import six.liang.service.ProductDiscountService;
import six.pinhong.model.Product;

@Controller
public class ProductDiscountController {

    @Autowired
    private ProductDiscountService productDiscountService;

    // 查全部
    @GetMapping("/GetAllProductDiscount")
    public String getAllProductDiscount(Model model) {
        List<ProductDiscount> products = productDiscountService.findAll();
        model.addAttribute("products", products);
        return "back/liang/GetAllProductDiscounts";
    }

    // 新增
    @GetMapping("/ProductDiscountInserts")
    public String showProductInsertForm() {
        return "back/liang/InsertProductDiscount";
    }

 
    @PostMapping("/ProductDiscountInsert")
    public String productInsert(@RequestParam("discountName") String discountName,
                                @RequestParam("discountPercentage") Integer discountPercentage,
                                @RequestParam("startDate") Date startDate,
                                @RequestParam("endDate") Date endDate,
                                @RequestParam("isActive") Integer isActive,
                                @RequestParam("productId") Integer productId,
                                RedirectAttributes redirectAttributes) {

        // 檢查 productId 是否已經存在
        if (productDiscountService.existsByProductId(productId)) {
            redirectAttributes.addFlashAttribute("error", "該商品已適用其他優惠，請選擇其他商品。");
            return "redirect:/ProductDiscountInserts";
        }

        ProductDiscount productDiscount = new ProductDiscount();
        productDiscount.setDiscountName(discountName);
        productDiscount.setDiscountPercentage(discountPercentage);
        productDiscount.setStartDate(startDate);
        productDiscount.setEndDate(endDate);
        productDiscount.setIsActive(isActive);

        
        Product product = new Product();
        product.setProductId(productId);
        productDiscount.setProduct(product);

        productDiscountService.insert(productDiscount);

        return "redirect:/GetAllProductDiscount";
    }

    // 刪除
    @DeleteMapping("/productDiscount/delete")
    public ResponseEntity<String> deleteProductDiscount(@RequestParam Integer id) {
        System.out.println("Deleting discountId: " + id);
        try {
            productDiscountService.deleteById(id);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
        }
    }

    // 更新
    @GetMapping("/ProductDiscountUpdates")
    public String showProductUpdateForm(@RequestParam("discountId") Integer discountId, Model model) {
        ProductDiscount productDiscount = productDiscountService.findById(discountId);
        model.addAttribute("productDiscount", productDiscount);
        return "back/liang/UpdateProductDiscount";
    }

    @PostMapping("/ProductDiscountUpdate")
    public String productUpdate(@RequestParam("discountId") Integer discountId,
                                @RequestParam("discountName") String discountName,
                                @RequestParam("discountPercentage") Integer discountPercentage,
                                @RequestParam("startDate") Date startDate,
                                @RequestParam("endDate") Date endDate,
                                @RequestParam("isActive") Integer isActive,
                                @RequestParam("productId") Integer productId) {

        ProductDiscount productDiscount = productDiscountService.findById(discountId);
        productDiscount.setDiscountName(discountName);
        productDiscount.setDiscountPercentage(discountPercentage);
        productDiscount.setStartDate(startDate);
        productDiscount.setEndDate(endDate);
        productDiscount.setIsActive(isActive);

        Product product = new Product();
        product.setProductId(productId);
        productDiscount.setProduct(product);

        productDiscountService.update(productDiscount);
        return "redirect:/GetAllProductDiscount";
    }

}

