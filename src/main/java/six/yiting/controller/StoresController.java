package six.yiting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import six.yiting.model.StoresBean;
import six.yiting.service.StoreService;

@Controller
public class StoresController {
	
	@Autowired
	private StoreService storeService;
	
	
	@GetMapping("/stores/findAll")
	public String findAllStores(Model model) {
		
		List<StoresBean> listStores = storeService.findAllStores();
		
		model.addAttribute("listStores",listStores);
		
		
		return "back/yiting/GetAllStores";
	}
	
	@GetMapping("/stores/findAllAjax")
	@ResponseBody
	public List<StoresBean> findAllStoresAjax() {
		
		return storeService.findAllStores();
		
	}
	
	@GetMapping("/store/addStorePage")
    public String storeInsertPage(Model m) {
		m.addAttribute("store", new StoresBean());
		return "back/yiting/insertStore";
		
	}
	
	@PostMapping("/store/insertStore")
    public String storeInsert(@ModelAttribute("store")StoresBean store,Model model) {
		storeService.saveStore(store);
		model.addAttribute("store",store);
		return "redirect:/stores/findAll";
		
	}
	
	
	@DeleteMapping("/store/delete")
	@ResponseBody
	public String deleteStore(@RequestParam Integer id) {
	    storeService.deleteStore(id);
	    return "success"; 
	}
	
	@GetMapping("/store/edit")
	public String editStore(@RequestParam Integer id, Model model) {
		StoresBean store = storeService.findStoreById(id);
		
		model.addAttribute("store", store);
		
		return "back/yiting/editStore";
	}
	
	@PutMapping("/store/editPost")
	public String editPost(@ModelAttribute StoresBean store) {
		storeService.saveStore(store);
		
		return "redirect:/stores/findAll";
	}

}