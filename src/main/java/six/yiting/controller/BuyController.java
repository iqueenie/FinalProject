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

import jakarta.servlet.http.HttpSession;
import six.hsiao.dto.ManagementDTO;
import six.hsiao.model.ManagementRoles;
import six.hsiao.model.ManagementRolesRepository;
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
	
	@Autowired
	private ManagementRolesRepository managementRolesRepo;
	
	
	@GetMapping("/private/buy/findAll")
	public String findAllBuys(HttpSession session, Model model) {
		
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		
		if (managementRole.getStore()!=null) {
			List<BuyBean> listByStore = buyService.findByStore(managementRole.getStore());
			model.addAttribute("listBuys",listByStore);
		}else {
			List<BuyBean> listBuys = buyService.findAllBuys();
			model.addAttribute("listBuys",listBuys);
		}
		
		return "back/yiting/GetAllBuys";
	}
	
	@GetMapping("/private/buy/findAllAjax")
	@ResponseBody
	public List<BuyBean> findAllStoresAjax(HttpSession session) {
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		if (managementRole.getStore()!=null) {
			return buyService.findByStore(managementRole.getStore());
		}else {
			return buyService.findAllBuys();
		}
		
		
		
	}
	
	@GetMapping("/private/buy/addBuyPage")
    public String buyInsertPage(HttpSession session,Model m) {
		List<Product> products = productService.findAll();
		
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		
		if (managementRole.getStore()!=null) {
			m.addAttribute("stores",managementRole.getStore());
		}else {
			List<StoresBean> stores = storeService.findAllStores();
			m.addAttribute("stores",stores);
		}
		
		
		m.addAttribute("products", products);
		m.addAttribute("buy", new BuyBean());
		
		return "back/yiting/insertBuy";
		
	}
	
	@PostMapping("/private/buy/insertBuy")
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
			
			BuyBean buy=buyService.saveBuy(buybean);
			
			System.out.println("ProductIdLength: "+productId.length);
			for(int i=0;i<productId.length;i++) {
	 			DetailBean detail = new DetailBean();
	 			detail.setBuy(buy);
	 			detail.setPurchaseId(buy.getPurchaseId());
	 			Product product = productService.findProductById(productId[i]);
	 			detail.setProduct(product);
	 			 if (product.getProductName().equals("霜淇淋")) {
	 				detail.setPurchaseNum(Integer.valueOf(purchaseNum[i])*20);
                 }
	 			 else if (product.getProductName().equals("甜甜圈")) {
                	 detail.setPurchaseNum(Integer.valueOf(purchaseNum[i])*20);
                 }
	 			 else if (product.getProductName().equals("茶葉蛋")) {
                	 detail.setPurchaseNum(Integer.valueOf(purchaseNum[i])*10);
                 }
	 			 else if (product.getProductName().equals("烤番薯")) {
                	 detail.setPurchaseNum(Integer.valueOf(purchaseNum[i])*20);
                 }
	 			 else if (product.getProductName().equals("珍珠奶茶")) {
                	 detail.setPurchaseNum(Integer.valueOf(purchaseNum[i])*10);
                 }else {
                	 detail.setPurchaseNum(Integer.valueOf(purchaseNum[i]));
                 }
	 			
	 			
	 			detailService.saveDetail(detail);
			}
		
			return "redirect:/private/buy/findAll";
		}
	}
	
	
	@DeleteMapping("/private/buy/delete")
	@ResponseBody
	public String deleteBuy(@RequestParam("id") Integer id) {
	    buyService.deleteBuy(id);
	    detailService.deleteDetailByPurchaseId(id);
	    return "success"; 
	}
	
	
	
	@GetMapping("/private/buy/check")
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
			m.addAttribute("errorBuy","商品: "+productSelect+"，已存在存貨中，數量將加進原有存貨紀錄");
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
 			
 			invService.buyInsertInv(invbean);
		}
		
		
		return "redirect:/private/buy/findAll";
		}
	}

}
