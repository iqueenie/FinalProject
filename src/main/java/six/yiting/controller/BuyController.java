package six.yiting.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class BuyController {
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private BuyService buyService;
	
	@Autowired
	private DetailService detailService;
	
	@Autowired
	private InventoryService invService;
	
	
	@GetMapping("/buy/findAll")
	public String findAllBuys(Model model) {
		
		List<BuyBean> listBuys = buyService.findAllBuys();
		
		model.addAttribute("listBuys",listBuys);
		
		return "back/yiting/GetAllBuys";
	}
	
	@GetMapping("/buy/findAllAjax")
	@ResponseBody
	public List<BuyBean> findAllStoresAjax() {
		
		return buyService.findAllBuys();
		
	}
	
	@GetMapping("/buy/addBuyPage")
    public String buyInsertPage(Model m) {
		List<Product> products = productService.findAll();
		List<StoresBean> stores = storeService.findAllStores();
		
		m.addAttribute("products", products);
		m.addAttribute("stores", stores);
		m.addAttribute("buy", new BuyBean());
		
		return "back/yiting/insertBuy";
		
	}
	
	@PostMapping("/buy/insertBuy")
    public String buyInsert(@RequestParam("arrivedDate") @DateTimeFormat(pattern = "yyyy/MM/dd")LocalDate arrivedDate,
			@RequestParam("buyStoreId") Integer storeId,
			@RequestParam("productId[]") Integer[] productId,
			@RequestParam("purchaseNum[]") Integer[] purchaseNum, Model m) {
		
		boolean result = buyService.checkBuyExist(arrivedDate, storeService.findStoreById(storeId));
		
		if(result) {
			List<Product> products = productService.findAll();
			List<StoresBean> stores = storeService.findAllStores();
			
			m.addAttribute("products", products);
			m.addAttribute("stores", stores);
			m.addAttribute("buy", new BuyBean());
			m.addAttribute("errorMsg","已存在此筆採購單，請重新輸入!!");
	        return "back/yiting/insertBuy";
		}else {
			BuyBean buybean= new BuyBean();
			buybean.setArrivedDate(arrivedDate);
			buybean.setStore(storeService.findStoreById(storeId));
			
			BuyBean buy=buyService.saveBuy(buybean); // 假设这里是调用业务逻辑来保存数据的方法
			
			System.out.println("ProductIdLength: "+productId.length);
			for(int i=0;i<productId.length;i++) {
	 			DetailBean detail = new DetailBean();
	 			detail.setBuy(buy);
	 			detail.setPurchaseId(buy.getPurchaseId());
	 			detail.setProduct(productService.findProductById(productId[i]));
	 			detail.setPurchaseNum(Integer.valueOf(purchaseNum[i]));
	 			detailService.saveDetail(detail);
			}
		
			return "redirect:/buy/findAll";
		}
	}
	
	
	@DeleteMapping("/buy/delete")
	@ResponseBody
	public String deleteBuy(@RequestParam("id") Integer id) {
	    buyService.deleteBuy(id);
	    detailService.deleteDetailByPurchaseId(id);
	    return "success"; 
	}
	
	
	
	@GetMapping("/buy/check")
    public String buyCheck(@RequestParam("id") Integer id,Model m) {
		
		BuyBean buy = buyService.findBuyById(id);
		
		
		
		List<DetailBean> details = detailService.findByPurchaseId(id);
		
		
		String productSelect="";
		boolean finalResult=false;
		for(DetailBean detail:details) {
			boolean result = invService.checkInvExist(detail.getBuy().getArrivedDate(),detail.getProduct().getProductId(),detail.getBuy().getStore());
			if(result) {
				finalResult=true;
				productSelect=productSelect+"、"+detail.getProduct().getProductName();
				
				
			}
		}
		
		if(finalResult) {
			m.addAttribute("hasError", true);
			m.addAttribute("errorBuy","商品: "+productSelect+"，已存在存貨中，將覆蓋原有存貨紀錄");
			List<BuyBean> listBuys = buyService.findAllBuys();
			m.addAttribute("listBuys",listBuys);
			m.addAttribute("id",id);
			
			
			return "back/yiting/GetAllBuys";
		}else {
		
		for(DetailBean detail:details) {
			
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
		
		
		return "redirect:/buy/findAll";
		}
	}

}
