package six.yiting.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import six.pinhong.model.Product;
import six.pinhong.service.ProductService;
import six.yiting.model.BuyBean;
import six.yiting.model.DetailBean;
import six.yiting.model.StoresBean;
import six.yiting.service.BuyService;
import six.yiting.service.DetailService;
import six.yiting.service.StoreService;

@Controller
public class DetailController {
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private BuyService buyService;
	
	@Autowired
	private DetailService detailService;
	
	
	@GetMapping("/private/detail/getDetail")
	public String getDetail(@RequestParam("purchaseId")Integer pid,Model m) {
	
			List<DetailBean> details = detailService.findByPurchaseId(pid);
			
			m.addAttribute("details", details);
			m.addAttribute("purchaseId",pid);
			m.addAttribute("buyBean",buyService.findBuyById(pid));
		
			return "back/yiting/GetSelectDetail";
	}

	
	@GetMapping("/private/detail/editPage")
    public String detailInsertPage(@ModelAttribute("purchaseId")Integer pid,Model m) {
		List<Product> products = productService.findAll();
		List<DetailBean> details = detailService.findByPurchaseId(pid);

		m.addAttribute("products", products);
		m.addAttribute("buy",buyService.findBuyById(pid));
		m.addAttribute("products", products);
		m.addAttribute("details", details);
		return "back/yiting/editDetail";
		
	}
	
	
	@PutMapping("/private/detail/editPost")
	public String editPost(@RequestParam("arrivedDate") @DateTimeFormat(pattern = "yyyy/MM/dd")LocalDate arrivedDate,
			@RequestParam("buyStoreId") Integer storeId,
			@RequestParam("productId[]") Integer[] productId,
			@RequestParam("purchaseNum[]") Integer[] purchaseNum,
			@RequestParam("purchaseId") Integer purchaseId,
			@RequestParam("orgArrivedDate")@DateTimeFormat(pattern = "yyyy/MM/dd")LocalDate orgDate,Model m) {
		
		
		boolean result = buyService.checkBuyExist(arrivedDate, storeService.findStoreById(storeId));
		if(orgDate.equals(arrivedDate)) {
			result=false;
		}
		
		if(result) {
			List<Product> products = productService.findAll();
			List<DetailBean> details = detailService.findByPurchaseId(purchaseId);

			m.addAttribute("products", products);
			m.addAttribute("buy",buyService.findBuyById(purchaseId));
			m.addAttribute("products", products);
			m.addAttribute("details", details);
			m.addAttribute("errorMsg","此日期的採購單已存在，請重新修改!!");
	        return "back/yiting/editDetail";
		}else {
		
			BuyBean buybean = buyService.findBuyById(purchaseId);
			buybean.setPurchaseId(purchaseId);
			buybean.setArrivedDate(arrivedDate);
			buybean.setStore(storeService.findStoreById(storeId));
	
			List<DetailBean> details=detailService.findByPurchaseId(purchaseId);
			for(DetailBean detail : details) {
				if(detail!=null) {
					detailService.deleteDetail(detail.getBuyRecId());
				}
			}
			for(int i=0;i<productId.length;i++) {
				if(productId[i]!=null && purchaseNum[i]!=null) {
		 			DetailBean detail = new DetailBean();
		 			detail.setBuy(buybean);
		 			detail.setPurchaseId(purchaseId);
		 			detail.setProduct(productService.findProductById(productId[i]));
		 			detail.setPurchaseNum(Integer.valueOf(purchaseNum[i]));
		 			detailService.saveDetail(detail);
				}
			}
			buyService.saveBuy(buybean);
			
			return "redirect:/private/buy/findAll";
		}
	}

}
