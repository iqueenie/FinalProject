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
	@Autowired
	private ManagementRolesRepository managementRolesRepo;
	
	
	@GetMapping("/private/inventory/findAll")
	public String findAllInventory(HttpSession session,Model model) {
		
		
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		
		if (managementRole.getStore()!=null) {
			List<InventoryBean> findbyStore = invService.findByStore(managementRole.getStore());
			model.addAttribute("listInvs",findbyStore);
		}else {
			List<InventoryBean> listInvs = invService.findAllInventory();
			model.addAttribute("listInvs",listInvs);
		}

		return "back/yiting/GetAllInventory";
	}
	
	@GetMapping("/private/inventory/findAllAjax")
	@ResponseBody
	public List<InventoryBean> findAllInvAjax(HttpSession session) {
		
		
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		
		if (managementRole.getStore()!=null) {
			return invService.findByStore(managementRole.getStore());
		}else {
			return invService.findAllInventory();
			
		}
		
	}
	
	@GetMapping("/private/inventory/addInvPage")
    public String invInsertPage(HttpSession session,Model m) {
		List<Product> products = productService.findAll();
		List<StoresBean> stores = storeService.findAllStores();
		
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		
		if (managementRole.getStore()!=null) {
			m.addAttribute("stores", managementRole.getStore());
		}else {
			m.addAttribute("stores", stores);
		}
		
		m.addAttribute("products", products);
		m.addAttribute("inventory", new InventoryBean());
		return "back/yiting/insertInv";
		
	}
	
	@PostMapping("/private/inventory/insertInv")
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
				
				Product product = productService.findProductById(productId[i]);
				
				 if (product.getProductName().equals("霜淇淋")) {
					 	invbean.setInvNum(Integer.valueOf(invNum[i])*20);
	                 }
		 			 else if (product.getProductName().equals("甜甜圈")) {
		 				invbean.setInvNum(Integer.valueOf(invNum[i])*20);
	                 }
		 			 else if (product.getProductName().equals("茶葉蛋")) {
		 				invbean.setInvNum(Integer.valueOf(invNum[i])*10);
	                 }
		 			 else if (product.getProductName().equals("烤番薯")) {
		 				invbean.setInvNum(Integer.valueOf(invNum[i])*20);
	                 }
		 			 else if (product.getProductName().equals("珍珠奶茶")) {
		 				invbean.setInvNum(Integer.valueOf(invNum[i])*10);
	                 }else {
	                	 invbean.setInvNum(Integer.valueOf(invNum[i]));
	                 }

	 			
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
				return "redirect:/private/inventory/findAll";
		}
	}
	
	
	@GetMapping("/private/inventory/minusOne")
	public String minusOneInventory(@RequestParam("storeId") Integer storeId,
			@RequestParam("buyCode") String buyCode) {
		
		invService.minusOneInventory(storeId, buyCode);
		return "redirect:/private/inventory/findAll";
	}
	
	
	@GetMapping("/private/inventory/addByBuy")
	@ResponseBody
    public String invBuyCheck(@RequestParam("id") Integer id,Model m) {
		
		BuyBean buy = buyService.findBuyById(id);
		
		
		
		List<DetailBean> details = detailService.findByPurchaseId(id);
		
		
		for(DetailBean detail:details) {
			boolean result = invService.checkInvExist(detail.getBuy().getArrivedDate(),detail.getProduct().getProductId(),detail.getBuy().getStore());
			if(result) {
				LocalDate deliveryDate = detail.getBuy().getArrivedDate();
				InventoryBean invbean = invService.findInvCondition(deliveryDate,detail.getProduct().getProductId(), detail.getBuy().getStore());
				int invNum = invbean.getInvNum();
				invbean.setDeliveryDate(deliveryDate);
				int storeId = detail.getBuy().getStore().getStoreId();
				invbean.setStore(storeService.findStoreById(storeId));
				invbean.setProductId(detail.getProduct().getProductId());
				invbean.setProduct(detail.getProduct());
				invbean.setInvNum(detail.getPurchaseNum()+invNum);
				
	 			
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
	
	
	
	@DeleteMapping("/private/inventory/delete")
	@ResponseBody
	public String deleteInv(@RequestParam Integer id) {
	   invService.deleteInventory(id);
	    return "success"; 
	}
	
	@GetMapping("/private/inventory/edit")
	public String editInv(@RequestParam Integer id, Model model) {
		InventoryBean inv = invService.findInvById(id);
		
		model.addAttribute("inv", inv);
		
		return "back/yiting/editInv";
	}
	
	@PutMapping("/private/inventory/editPost")
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
			
			invService.updateInv(invbean);
			
			return "redirect:/private/inventory/findAll";
		}
	}

}
