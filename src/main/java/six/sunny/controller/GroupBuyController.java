package six.sunny.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import six.pinhong.model.Product;
import six.pinhong.model.ProductRepository;
import six.pinhong.service.ProductService;
import six.sunny.model.GroupBuy;
import six.sunny.model.StoreNameRepository;
import six.sunny.service.GroupBuyService;
import six.yiting.model.StoresBean;

@Controller
public class GroupBuyController {
	
	@Autowired
	private GroupBuyService groupBuyService;
	
//	TODO
	@Autowired
	private ProductRepository productService;
	
//	TODO
	@Autowired
	private StoreNameRepository storeService;
	
	@RequestMapping(path = "private/GetAllGroupBuy", method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})
	public String getAllGroupBuy(Model m) {

//		List<GroupBuyBean> gbs = null;
//
//		if(storeId.isEmpty() && productId.isEmpty()) {
		List<GroupBuy> gbs = groupBuyService.findAll();	
//		}else if(!storeId.isEmpty() && !productId.isEmpty()) {
//			gbs = groupBuyService.getByProductStoreId(Integer.parseInt(productId), Integer.parseInt(storeId));
//		}else if (!storeId.isEmpty()) {
//			gbs = groupBuyService.getByStoreId(Integer.parseInt(storeId));
//		}else if (!productId.isEmpty()) {
//			gbs = groupBuyService.getByProductId(Integer.parseInt(productId));
//		}

//		右上角查詢下拉式選單用的store和product清單
		List<StoresBean> stns = storeService.findAll();
		List<Product> pdns = productService.findAll();
		
		m.addAttribute("gbs", gbs);
		m.addAttribute("stns", stns);
		m.addAttribute("pdns", pdns);
		
		return "/back/sunny/GetAllGroupBuy";
	}
	
	@ResponseBody
	@GetMapping("private/FindByProductIdAndStoreId")
	public List<GroupBuy> findByProductIdAndStoreId(
			@RequestParam(value = "storeId", defaultValue = "") String storeId, 
			@RequestParam(value = "productId", defaultValue = "") String productId) {
		
		List<GroupBuy> gbs = null;

		if(storeId.isEmpty() && productId.isEmpty()) {
			gbs = groupBuyService.findAll();	
		}else if(!storeId.isEmpty() && !productId.isEmpty()) {
			gbs = groupBuyService.findByProductIdAndStoreId(Integer.parseInt(productId), Integer.parseInt(storeId));
		}else if (!storeId.isEmpty()) {
			gbs = groupBuyService.findByStoreId(Integer.parseInt(storeId));
		}else if (!productId.isEmpty()) {
			gbs = groupBuyService.findByProductId(Integer.parseInt(productId));
		}
		
		return gbs;
		
	}
	
	@GetMapping("private/InsertGroupBuyForm")
	public String insertGroupBuyForm(Model m) {
//		取得表單填寫所需的List
		List<Product> pdns = productService.findAll();
		List<StoresBean> stns = storeService.findAll();
		
		m.addAttribute("pdns", pdns);
		m.addAttribute("stns", stns);
		m.addAttribute("groupBuy", new GroupBuy());
		
		return "back/sunny/InsertGroupBuy";
	}
	
	@PostMapping("private/InsertGroupBuy")
	public String insertGroupBuy(@ModelAttribute GroupBuy groupBuy) {
		groupBuyService.insert(groupBuy);
		return "redirect:/private/GetAllGroupBuy";
	}
	
	@ResponseBody
	@DeleteMapping("private/DeleteGroupBuy")
	public ResponseEntity<Void> deleteGroupBuy(@RequestParam("id") Integer id, Model m) {
		boolean isDeleted = groupBuyService.deleteById(id);
	    if (isDeleted) {
	        return ResponseEntity.noContent().build();
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@GetMapping("private/UpdateGroupBuyForm")
	public String updateGroupBuyForm(@RequestParam("id") Integer groupBuyId, Model m) {		
//		取得下拉式選單會用到的資料
		List<Product> pdns = productService.findAll();
		List<StoresBean> stns = storeService.findAll();
		
		GroupBuy nowGb = groupBuyService.findById(groupBuyId);
		
		m.addAttribute("pdns", pdns);
		m.addAttribute("stns", stns);
		m.addAttribute("groupBuy", nowGb);

		return "back/sunny/UpdateGroupBuy";
	}
	
	@PutMapping("private/UpdateGroupBuy")
	public String updateGroupBuy(@ModelAttribute("GroupBuyBean") GroupBuy groupBuyBean) {


		
//		更新
		groupBuyService.update(groupBuyBean);

		return "redirect:/private/GetAllGroupBuy";
	}
}
