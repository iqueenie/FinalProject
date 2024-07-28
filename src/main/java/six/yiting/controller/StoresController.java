package six.yiting.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.maps.model.LatLng;

import jakarta.servlet.http.HttpSession;
import six.hsiao.dto.ManagementDTO;
import six.hsiao.model.ManagementRepository;
import six.hsiao.model.ManagementRoles;
import six.hsiao.model.ManagementRolesRepository;
import six.pinhong.model.Product;
import six.yiting.dto.SelectProductDto;
import six.yiting.model.InventoryBean;
import six.yiting.model.StoresBean;
import six.yiting.service.GeocodingService;
import six.yiting.service.InventoryService;
import six.yiting.service.StoreService;

@Controller
public class StoresController {
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private GeocodingService geocodingService;
	
	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private ManagementRolesRepository managementRolesRepo;
	
	
	@GetMapping("/private/stores/findAll")
	public String findAllStores(Model model,HttpSession session) {
		
		List<StoresBean> listStores = storeService.findAllStores();
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		
		if (managementRole.getStore()!=null) {
			model.addAttribute("role", "店長");
		}else {
			model.addAttribute("role", "管理員");
		}
		
		model.addAttribute("listStores",listStores);
		
		
		return "back/yiting/GetAllStores";
	}
	
	@GetMapping("/private/stores/findAllAjax")
	@ResponseBody
	public List<StoresBean> findAllStoresAjax() {
		
		return storeService.findAllStores();
		
	}

	
	
	@GetMapping("/private/store/addStorePage")
    public String storeInsertPage(Model m) {
		m.addAttribute("store", new StoresBean());
		return "back/yiting/insertStore";
		
	}
	
	@PostMapping("/private/store/insertStore")
    public String storeInsert(@ModelAttribute("store")StoresBean store,Model model) {
		storeService.saveStore(store);
		model.addAttribute("store",store);
		return "redirect:/private/stores/findAll";
		
	}
	
	
	@DeleteMapping("/private/store/delete")
	@ResponseBody
	public String deleteStore(@RequestParam Integer id) {
	    storeService.deleteStore(id);
	    return "success"; 
	}
	
	@GetMapping("/private/store/edit")
	public String editStore(@RequestParam Integer id, Model model) {
		StoresBean store = storeService.findStoreById(id);
		
		model.addAttribute("store", store);
		
		return "back/yiting/editStore";
	}
	
	@PutMapping("/private/store/editPost")
	public String editPost(@ModelAttribute StoresBean store) {
		storeService.saveStore(store);
		
		return "redirect:/private/stores/findAll";
	}
	
	
	//前台店鋪搜尋
	@GetMapping("/public/front/storeSearch")
	public String StoreSearch(Model model) {
		return "front/yiting/StoreSearch";
	}
	
	//前台友善商品
	@GetMapping("/public/front/friendlyProduct")
	public String FriendlyProduct() {
		return "front/yiting/FriendlyProducts";
	}
	
	
	//用id找店鋪
	@GetMapping("/public/front/findById")
	@ResponseBody
	public StoresBean findStoreById(Integer storeId) {
		
		return storeService.findStoreById(storeId);
		
	}
	
	//用城市找店鋪
	@GetMapping("/public/front/findBycity")
	@ResponseBody
	public List<StoresBean> findByCity(String city){
		return storeService.findStoreByCity(city);
	}
	
	//用城市和區域找店鋪
	@GetMapping("/public/front/findByArea")
	@ResponseBody
	public List<StoresBean> findByArea(String city,String area){
		return storeService.findByCityAndArea(city, area);
	}
	
	//用城市和區域找店鋪分頁版
	@GetMapping("/public/front/findByAreaPage")
	@ResponseBody
	public Page<StoresBean> findByAreaPage(String city,String area){
		return storeService.findByCityAndAreaPage(city, area);
	}
	
	
	//用城市和區域和街道找店鋪
	@GetMapping("/public/front/findByStreet")
	@ResponseBody
	public List<StoresBean> findByStreet(String city,String area,String street){
		return storeService.findByCityAndAreaAndStreet(city, area, street);
	}
	
	//用城市和街道找店鋪
	@GetMapping("/public/front/findByCityStreet")
	@ResponseBody
	public List<StoresBean> findByCityStreet(String city,String street){
		return storeService.findByCityAndStreet(city, street);
	}
	
	

	//用城市找區域
	@GetMapping("/public/front/findAreaBycity")
	@ResponseBody
	public List<String> findAreaByCity(String city){
		return storeService.findAreaByCity(city);
	}
	
	//用區域找街道
	@GetMapping("/public/front/findStreetByArea")
	@ResponseBody
	public List<String> findStreetByArea(String city,String area){
		return storeService.findStreetByArea(city, area);
	}
	
	//用城市和區域算店鋪數
	@GetMapping("/public/front/countStores")
	@ResponseBody
	public long countStores(String city,String area){
		return storeService.countStores(city, area);
	}
	
	//用城市和區域和街道算店鋪數
	@GetMapping("/public/front/countStoreByStreet")
	@ResponseBody
	public long countStoreByStreet(String city,String area,String street){
		return storeService.countStoresByStreet(city, area, street);
	}
	
	//用城市和街道算店鋪數
	@GetMapping("/public/front/countByCityAndStreet")
	@ResponseBody
	public long countByCityAndStreet(String city,String street) {
		return storeService.countByCityAndStreet(city, street);
	}
	
	//用郵遞區號找縣市、區域
	@GetMapping("/public/front/findByZip")
	@ResponseBody
	public String[] findByZip(String cityNum) {
		
		 String byZip = storeService.findByZip(cityNum);
		 if(byZip!=null) {	 
			 String[] parts = byZip.split(",");
			 
				return parts;
			 }
			 return null;
		 
	}
	
	//模糊查詢找店鋪
	@GetMapping("/public/front/findByName")
	@ResponseBody
	public List<StoresBean> findByName(@RequestParam("storeName") String name) {
		return storeService.findByName(name);
	}
	
	//用模糊查詢結果算店鋪數
	@GetMapping("/public/front/countByWordName")
	@ResponseBody
	public long countByWordName(String storeName) {
		return storeService.countByWordName(storeName);
	}
	
	//地圖經緯度回傳
	 @GetMapping("/public/front/map")
	 @ResponseBody
    public LatLng getAddressMap(@RequestParam("id") Integer id, Model model) {
        String address = geocodingService.getAddressFromDatabase(id);
        return geocodingService.geocodeAddress(address);
    }
	 
	//實體店商品
	 @GetMapping("public/front/storeProduct")
	 @ResponseBody
	 public List<Product> getProductByType(){
		 return storeService.findByType();
	 }
	 
	 //符合勾選條件的店鋪
	 @PostMapping("/public/front/filterByProduct")
	 @ResponseBody
	 public List<StoresBean> handlePostData(@RequestBody SelectProductDto requestData) {
	        List<StoresBean> stores = requestData.getStores();
	        List<String> selectedItems = requestData.getSelectedItems();
	        List<StoresBean> resultList = new ArrayList<StoresBean>();
	        
	        for(StoresBean store:stores) {
	        	boolean check = true;
	        	for(String prdId:selectedItems) {
	        		int pid = Integer.parseInt(prdId);
	        		List<InventoryBean> invResult= inventoryService.findByStoreAndProduct(store, pid);
	        		 if(invResult.size()==0) {
	        			 check = false;
	        			 break;
	        		 }
	        	}
	        	if(check) {
	        		resultList.add(store);
	        	}
	        }
	        return resultList;
	    }
	 
	 //找出店鋪有哪些特殊商品
	 @PostMapping("/public/front/storeSpecial")
	 @ResponseBody
	 public List<Integer> getProductByStore(@RequestBody StoresBean store){
		List<Product> byType = storeService.findByType();
		List<Integer> resultList = new ArrayList<Integer>();
		for(Product product : byType) {
			List<InventoryBean> inv = inventoryService.findByStoreAndProduct(store, product.getProductId());
			if(inv.size()>=1) {
				resultList.add(product.getProductId());
			}
		}
		return resultList;
		
	 }
	 
	//用城市和區域找店鋪找存貨為當日Map<類別,數量>
	 @PostMapping("/public/front/friendlyProduct")
	 @ResponseBody
	 public Map<String,Integer> friendlyProductByStore(@RequestBody StoresBean store){
		List<Product> byType = storeService.findByOtherType();

		Map<String,Integer> inventoryExpDates = new HashMap<>();

		LocalDate today = LocalDate.of(2024, 8, 2);
		for(Product product : byType) {
			InventoryBean inv = inventoryService.findByStoreProductExp(store, product, today);
			String type = product.getProductType(); 
			if(inv!=null) {
				if(inventoryExpDates.containsKey(type)) {
				Integer count=inventoryExpDates.get(type);
				inventoryExpDates.put(type, count+inv.getInvNum());
				}else {
					inventoryExpDates.put(type,inv.getInvNum());
				}
			}
		}
		
		return inventoryExpDates;
		
	 }
	 
	 //友善時光分頁
	 @ResponseBody
	 @GetMapping("/front/friByCityAndArea")
	 public Page<StoresBean> friendlyProductPage(@RequestParam(value = "p", defaultValue = "1") Integer pageNumber,String city,String area){
		 Page<StoresBean> friProductByAreaAndCity = storeService.findFriProductByAreaAndCity(pageNumber, city, area);	
		 return friProductByAreaAndCity;
	 }
	 
	 //找出店鋪有哪些友善時光商品
	 @PostMapping("/public/front/storeFriendly")
	 @ResponseBody
	 public List<InventoryBean> getFriInvByStore(@RequestBody StoresBean store){
		List<Product> byType = storeService.findByOtherType();
		List<InventoryBean> resultList = new ArrayList<>();
		LocalDate today = LocalDate.of(2024, 8, 2);
		for(Product product : byType) {
			InventoryBean inv = inventoryService.findByStoreProductExp(store, product, today);
			if(inv!=null ) {
				resultList.add(inv);
			}
		}
		return resultList;
		
	 }
	 
	 //友善時光查詢單店結果頁面
	 @GetMapping("/public/front/friendlyResult")
	 public String FriendlyResult(@RequestParam(value = "p", defaultValue = "1") Integer pageNumber,
								 @RequestParam("storeId") Integer storeId,
								 @RequestParam(value ="productType", defaultValue = "全部") String productType,
								 Model model) {

		 //商品部分
//		 LocalDate today = LocalDate.now().minusDays(3);
		 LocalDate today = LocalDate.of(2024, 8, 2);
		 StoresBean store = storeService.findStoreById(storeId);
		 
		 List<String> productTypes = inventoryService.findProductType(store, today);
		 
		 if(productType.equals("全部")) {
			 Page<InventoryBean> invPage = inventoryService.friendlyDetail(pageNumber,store,today);
			 model.addAttribute("invPage",invPage);
		 }else {
			 Page<InventoryBean> invType = inventoryService.findByType(pageNumber, store, today, productType);
			 model.addAttribute("invPage",invType);
		 }
		 
		 //地圖部分
		 String address = geocodingService.getAddressFromDatabase(storeId);
	        LatLng latLng= geocodingService.geocodeAddress(address);
	        if (latLng != null) {
	            model.addAttribute("latitude", latLng.lat);
	            model.addAttribute("longitude", latLng.lng);
	        }
		 
		 
		 
		 model.addAttribute("storeId",storeId);
		 model.addAttribute("store",store);
		 model.addAttribute("productTypes", productTypes);
		 model.addAttribute("productType", productType);
		 
		 
		return "front/yiting/FriendlyProductDetail";
	}
		
	 

}
