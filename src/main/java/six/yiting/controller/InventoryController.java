package six.yiting.controller;

import java.time.LocalDate;
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
import six.yiting.model.InventoryBean;
import six.yiting.model.StoresBean;
import six.yiting.service.BuyService;
import six.yiting.service.DetailService;
import six.yiting.service.InventoryService;
import six.yiting.service.StoreService;

@Controller
public class InventoryController {
	
	@Autowired
	private InventoryService invService;
	@Autowired
	private StoreService storeService;
	@Autowired
	private ProductService productService;
	@Autowired
	private BuyService buyService;
	@Autowired
	private DetailService detailService;
	
	
	@GetMapping("/inventory/findAll")
	public String findAllInventory(Model model) {
		
		List<InventoryBean> listInvs = invService.findAllInventory();
		
		model.addAttribute("listInvs",listInvs);
		
		
		return "back/yiting/GetAllInventory";
	}
	
	@GetMapping("/inventory/findAllAjax")
	@ResponseBody
	public List<InventoryBean> findAllInvAjax() {
		
		return invService.findAllInventory();
		
	}
	
	@GetMapping("/inventory/addInvPage")
    public String invInsertPage(Model m) {
		List<Product> products = productService.findAll();
		List<StoresBean> stores = storeService.findAllStores();
		
		m.addAttribute("products", products);
		m.addAttribute("stores", stores);
		m.addAttribute("inventory", new InventoryBean());
		return "back/yiting/insertInv";
		
	}
	
	@PostMapping("/inventory/insertInv")
    public String invInsert(@RequestParam("deliveryDate") @DateTimeFormat(pattern = "yyyy/MM/dd")LocalDate deliveryDate,
			@RequestParam("storeId") Integer storeId,
			@RequestParam("productId[]") Integer[] productId,
			@RequestParam("invNum[]") Integer[] invNum, Model m) {
		
		
		String productSelect="";
		boolean finalResult=false;
		for(int i=0;i<productId.length;i++) {
			boolean result = invService.checkInvExist(deliveryDate,productId[i],storeService.findStoreById(storeId));
			if(result) {
				finalResult=true;
				productSelect=productSelect+"、"+productService.findProductById(productId[i]).getProductName();
				
			}
		}
		
		if(finalResult) {
			List<Product> products = productService.findAll();
			List<StoresBean> stores = storeService.findAllStores();
			
			m.addAttribute("products", products);
			m.addAttribute("stores", stores);
			m.addAttribute("inventory", new InventoryBean());
			m.addAttribute("errorMsg","商品: "+productSelect+"，已存在存貨中，請重新輸入");
			return "back/yiting/insertInv";
		}else{
			for(int i=0;i<productId.length;i++) {
			
				InventoryBean invbean= new InventoryBean();
				invbean.setDeliveryDate(deliveryDate);
				invbean.setStore(storeService.findStoreById(storeId));
				invbean.setProductId(productId[i]);
				invbean.setProduct(productService.findProductById(productId[i]));
				invbean.setInvNum(invNum[i]);
				
	 			
	 			int days = productService.findProductById(productId[i]).getProductExpirydate();
	 			LocalDate expDate = deliveryDate.plusDays(days);
	 			invbean.setExpDate(expDate);
	 			String buycode = Integer.toString(productId[i]) + expDate.toString();
				buycode = buycode.replace("-", "");
				if (buycode.length() < 12) {
					buycode = String.format("%1$" + 12 + "s", buycode).replace(' ', '0');
				}
				invbean.setBuyCode(buycode);
	 			
	 			invService.saveInventory(invbean);
			}
				return "redirect:/inventory/findAll";
		}
	}
	
	
	@GetMapping("/inventory/addByBuy")
	@ResponseBody
    public String invBuyCheck(@RequestParam("id") Integer id,Model m) {
		
		BuyBean buy = buyService.findBuyById(id);
		
		
		
		List<DetailBean> details = detailService.findByPurchaseId(id);
		
		
		for(DetailBean detail:details) {
			boolean result = invService.checkInvExist(detail.getBuy().getArrivedDate(),detail.getProduct().getProductId(),detail.getBuy().getStore());
			if(result) {
				LocalDate deliveryDate = detail.getBuy().getArrivedDate();
				InventoryBean invbean = invService.findInvCondition(deliveryDate,detail.getProduct().getProductId(), detail.getBuy().getStore());
				invbean.setDeliveryDate(deliveryDate);
				int storeId = detail.getBuy().getStore().getStoreId();
				invbean.setStore(storeService.findStoreById(storeId));
				invbean.setProductId(detail.getProduct().getProductId());
				invbean.setProduct(detail.getProduct());
				invbean.setInvNum(detail.getPurchaseNum());
				
	 			
	 			int days =detail.getProduct().getProductExpirydate();
	 			LocalDate expDate = deliveryDate.plusDays(days);
	 			invbean.setExpDate(expDate);
	 			String buycode = Integer.toString(detail.getProduct().getProductId()) + expDate.toString();
				buycode = buycode.replace("-", "");
				if (buycode.length() < 12) {
					buycode = String.format("%1$" + 12 + "s", buycode).replace(' ', '0');
				}
				invbean.setBuyCode(buycode);
				buy.setCheckToInv(true);
				buyService.saveBuy(buy);
				
				invService.saveInventory(invbean);
			}else {
				InventoryBean invbean= new InventoryBean();
				LocalDate deliveryDate = detail.getBuy().getArrivedDate();
				invbean.setDeliveryDate(deliveryDate);
				int storeId = detail.getBuy().getStore().getStoreId();
				invbean.setStore(storeService.findStoreById(storeId));
				invbean.setProductId(detail.getProduct().getProductId());
				invbean.setProduct(detail.getProduct());
				invbean.setInvNum(detail.getPurchaseNum());
				
	 			
	 			int days =detail.getProduct().getProductExpirydate();
	 			LocalDate expDate = deliveryDate.plusDays(days);
	 			invbean.setExpDate(expDate);
	 			String buycode = Integer.toString(detail.getProduct().getProductId()) + expDate.toString();
				buycode = buycode.replace("-", "");
				if (buycode.length() < 12) {
					buycode = String.format("%1$" + 12 + "s", buycode).replace(' ', '0');
				}
				invbean.setBuyCode(buycode);
				buy.setCheckToInv(true);
				buyService.saveBuy(buy);
	 			
	 			invService.saveInventory(invbean);
			}
		}
		return "Success";
	}
	
	
	
	@DeleteMapping("/inventory/delete")
	@ResponseBody
	public String deleteInv(@RequestParam Integer id) {
	   invService.deleteInventory(id);
	    return "success"; 
	}
	
	@GetMapping("/inventory/edit")
	public String editInv(@RequestParam Integer id, Model model) {
		InventoryBean inv = invService.findInvById(id);
		
		model.addAttribute("inv", inv);
		
		return "back/yiting/editInv";
	}
	
	@PutMapping("/inventory/editPost")
	public String editPost(@RequestParam("deliveryDate") @DateTimeFormat(pattern = "yyyy/MM/dd")LocalDate deliveryDate,
			@RequestParam("storeId") Integer storeId,
			@RequestParam("productId") Integer productId,
			@RequestParam("invNum") Integer invNum,
			@RequestParam("invId") Integer invId,
			@RequestParam("orgDeliveryDate") @DateTimeFormat(pattern = "yyyy/MM/dd")LocalDate orgDate,Model m) {
		
		boolean result = invService.checkInvExist(deliveryDate,productId,storeService.findStoreById(storeId));
		if(orgDate.equals(deliveryDate)) {
			result=false;
		}
		
		if(result) {
			InventoryBean inv = invService.findInvById(invId);
			m.addAttribute("inv", inv);
			m.addAttribute("errorMsg","此日期的存貨已存在，請重新修改!!");
	        return "back/yiting/editInv";
		}else {
		
			InventoryBean invbean= invService.findInvById(invId);
			invbean.setDeliveryDate(deliveryDate);
			invbean.setStore(storeService.findStoreById(storeId));
			invbean.setProductId(productId);
			invbean.setProduct(productService.findProductById(productId));
			invbean.setInvNum(invNum);
			
				
			int days = productService.findProductById(productId).getProductExpirydate();
			LocalDate expDate = deliveryDate.plusDays(days);
			invbean.setExpDate(expDate);
			
			String buycode = Integer.toString(productId) + expDate.toString();
			buycode = buycode.replace("-", "");
			if (buycode.length() < 12) {
				buycode = String.format("%1$" + 12 + "s", buycode).replace(' ', '0');
			}
			invbean.setBuyCode(buycode);
			
			invService.saveInventory(invbean);
			
			return "redirect:/inventory/findAll";
		}
	}

}