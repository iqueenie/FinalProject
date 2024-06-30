package six.sunny.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import six.pinhong.model.Product;
import six.sunny.model.GroupBuyBean;
import six.sunny.service.GroupBuyService;
import six.yiting.model.StoresBean;

@Controller
public class GroupBuyController {
	
	@Autowired
	private GroupBuyService groupBuyService;
	
	@RequestMapping(path = "/GetAllGroupBuy", method = {RequestMethod.GET,RequestMethod.POST})
	public String getAllGroupBuy(@RequestParam(value = "storeGetId", defaultValue = "") String storeId, 
								@RequestParam(value = "productGetId", defaultValue = "") String productId,
								Model m) {

//		List<GroupBuyBean> gbs = null;
//
//		if(storeId.isEmpty() && productId.isEmpty()) {
		List<GroupBuyBean> gbs = groupBuyService.findAll();	
//		}else if(!storeId.isEmpty() && !productId.isEmpty()) {
//			gbs = groupBuyService.getByProductStoreId(Integer.parseInt(productId), Integer.parseInt(storeId));
//		}else if (!storeId.isEmpty()) {
//			gbs = groupBuyService.getByStoreId(Integer.parseInt(storeId));
//		}else if (!productId.isEmpty()) {
//			gbs = groupBuyService.getByProductId(Integer.parseInt(productId));
//		}
//	
////		右上角查詢下拉式選單用的store和product清單
//		List<StoresBean> stns = storeService.findAll();
//		List<Product> pdns = productService.findAll();
		
		m.addAttribute("gbs", gbs);
//		m.addAttribute("stns", stns);
//		m.addAttribute("pdns", pdns);
		
		return "sunny/GetAllGroupBuy";
	}
//	
//	@GetMapping("/InsertGroupBuyForm")
//	public String insertGroupBuyForm(Model m) {
////		取得表單填寫所需的List
//		List<Product> pdns = productService.findAll();
//		List<StoresBean> stns = storeService.findAll();
//		
//		m.addAttribute("pdns", pdns);
//		m.addAttribute("stns", stns);
//		m.addAttribute("GroupBuyBean", new GroupBuyBean());
//		
//		return "sunny/InsertGroupBuy";
//	}
//	
//	@PostMapping("/InsertGroupBuy")
//	public String insertGroupBuy(@ModelAttribute("GroupBuyBean") GroupBuyBean groupBuyBean) {
//		
//		Product product = productService.findProductById(groupBuyBean.getProductId());
//		StoresBean store = storeService.findById(groupBuyBean.getStoreId());
//		
//		groupBuyBean.setProduct(product);
//		groupBuyBean.setStore(store);
//		
//		groupBuyService.insert(groupBuyBean);
//		return "forward:/GetAllGroupBuy";
//	}
//	
//	@GetMapping("/DeleteGroupBuy")
//	public String deleteGroupBuy(@RequestParam("id") Integer id, Model m) {
//		groupBuyService.deleteById(id);
//		return "forward:/GetAllGroupBuy";
//	}
//	
//	@GetMapping("/UpdateGroupBuyForm")
//	public String updateGroupBuyForm(@RequestParam("id") Integer groupBuyId, Model m) {		
////		取得下拉式選單會用到的資料
//		List<Product> pdns = productService.findAll();
//		List<StoresBean> stns = storeService.findAll();
//		
//		GroupBuyBean nowGb = groupBuyService.findById(groupBuyId);
//		
//		m.addAttribute("pdns", pdns);
//		m.addAttribute("stns", stns);
//		m.addAttribute("GroupBuyBean", nowGb);
//
//		return "sunny/UpdateGroupBuy";
//	}
//	
//	@PostMapping("/UpdateGroupBuy")
//	public String updateGroupBuy(@ModelAttribute("GroupBuyBean") GroupBuyBean groupBuyBean) {
//
//		Product product = productService.findProductById(groupBuyBean.getProductId());
//		StoresBean store = storeService.findById(groupBuyBean.getStoreId());
//		
//		groupBuyBean.setProduct(product);
//		groupBuyBean.setStore(store);
//		
////		更新
//		groupBuyService.update(groupBuyBean);
//
//		return "forward:/GetAllGroupBuy";
//	}
}
