package six.yiting.controller;

import java.awt.Dialog.ModalExclusionType;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.servlet.http.HttpSession;
import six.hsiao.dto.ManagementDTO;
import six.hsiao.model.ManagementRoles;
import six.hsiao.model.ManagementRolesRepository;
import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;
import six.pinhong.model.Product;
import six.yiting.dto.InventoryCheckDto;
import six.yiting.dto.ProductTypeNumberDto;
import six.yiting.model.BuyBean;
import six.yiting.model.DetailBean;
import six.yiting.model.InventoryBean;
import six.yiting.model.StoreLikeBean;
import six.yiting.model.StoresBean;
import six.yiting.service.InventoryService;
import six.yiting.service.StoreLikeService;
import six.yiting.service.StoreService;

@Controller
public class StoreLikeController {
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private StoreLikeService likeService;
	
	@Autowired
	private MembersService membersService;
	
	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private ManagementRolesRepository managementRolesRepo;
	
	@GetMapping("/private/like/findAll")
	public String findAllLikes(HttpSession session,Model model) {
	
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		
		if (managementRole.getStore()!=null) {
			model.addAttribute("role", "店長");
		}else {
			model.addAttribute("role", "管理員");
		}
		
		List<StoreLikeBean> listLikes = likeService.findAllLikes();
		
		model.addAttribute("listLikes",listLikes);
		return "back/yiting/GetAllStoreLikes";
	}
	
	@GetMapping("/private/like/findAllAjax")
	@ResponseBody
	public List<StoreLikeBean> findAllStoresAjax() {
		
		return likeService.findAllLikes();
		
	}
	
	@GetMapping("/private/like/addLikePage")
    public String storeInsertPage(Model m) {
		List<StoresBean> stores = storeService.findAllStores();
		List<MembersBean> members = membersService.findAll();
		m.addAttribute("stores", stores);
		m.addAttribute("members", members);
		m.addAttribute("like", new StoreLikeBean());
		return "back/yiting/insertLike";
		
	}
	
	@PostMapping("/private/like/insertLike")
    public String storeInsert(@RequestParam("storeId") Integer storeId,
			@RequestParam("memberId") Integer memberId,Model m) {
		
		StoresBean store = storeService.findStoreById(storeId);
		MembersBean member = membersService.findByMemberId(memberId);
		
		boolean result = likeService.checkLikeExist(store, member);
		
		if(result) {
			List<StoresBean> stores = storeService.findAllStores();
			List<MembersBean> members = membersService.findAll();
			m.addAttribute("stores", stores);
			m.addAttribute("members", members);
			m.addAttribute("like", new StoreLikeBean());
			m.addAttribute("errorMsg","已存在此筆紀錄，請重新輸入!!");
	        return "back/yiting/insertLike";
		}else {
			StoreLikeBean like = new StoreLikeBean();
			like.setMember(member);
			like.setStore(store);
			likeService.saveLike(like);
			
			return "redirect:/private/like/findAll";
		}
		
	}
	
	@PostMapping("/public/front/insertLike")
	@ResponseBody
	public String insertLike(HttpSession session, @RequestBody Map<String, Integer> requestBody) {
	    Integer storeId = requestBody.get("storeId");
	    if (session.getAttribute("loggedInMember") == null) {
	        return null;
	    } else {
	        MembersBean member = (MembersBean) session.getAttribute("loggedInMember");
	        StoresBean store = storeService.findStoreById(storeId);
	        StoreLikeBean like = new StoreLikeBean();
	        like.setMember(member);
	        like.setStore(store);
	        likeService.saveLike(like);
	        return "success";
	    }
	}
	
	
	@DeleteMapping("/private/like/delete")
	@ResponseBody
	public String deleteStore(@RequestParam Integer id) {
	    likeService.deleteStoreLike(id);
	    return "success"; 
	}
	

	
	@GetMapping("/private/like/edit")
	public String editLike(@RequestParam Integer id, Model model) {
		
		StoreLikeBean like = likeService.findLikeById(id);
		List<StoresBean> stores = storeService.findAllStores();
		List<MembersBean> members = membersService.findAll();
		model.addAttribute("stores", stores);
		model.addAttribute("members", members);
		model.addAttribute("like", like);
		
		return "back/yiting/editLike";
	}
	
	@PutMapping("/private/like/editPost")
	public String editPost(@RequestParam("storeId") Integer storeId,
			@RequestParam("memberId") Integer memberId,@RequestParam("likeId") Integer likeId,Model m) {
		StoresBean store = storeService.findStoreById(storeId);
		MembersBean member = membersService.findByMemberId(memberId);
		
		boolean result = likeService.checkLikeExist(store, member);
		
		if(result) {
			StoreLikeBean like = likeService.findLikeById(likeId);
			List<StoresBean> stores = storeService.findAllStores();
			List<MembersBean> members = membersService.findAll();
			m.addAttribute("stores", stores);
			m.addAttribute("members", members);
			m.addAttribute("like", like);
			m.addAttribute("errorMsg","已存在此筆紀錄，請重新輸入!!");
			return "back/yiting/editLike";
		}else {
			StoreLikeBean like = likeService.findLikeById(likeId);
			like.setMember(member);
			like.setStore(store);
			likeService.saveLike(like);
			
			return "redirect:/private/like/findAll";
		}
	}
	
	@GetMapping("/private/like/count")
	@ResponseBody
	public Map<String, Object> countLike() {
	    List<StoresBean> listStores = storeService.findAllStores();
	    List<String> storeNames = new ArrayList<>();
	    List<Long> likeCounts = new ArrayList<>();
	    
	    for (StoresBean store : listStores) {
	        long likeNum = likeService.countLikes(store);
	        if (likeNum > 0) {
	            storeNames.add(store.getStoreName());
	            likeCounts.add(likeNum);
	        }
	    }
	    
	    Map<String, Object> response = new HashMap<>();
	    response.put("storeNames", storeNames);
	    response.put("likeCounts", likeCounts);
	    
	    return response;
	}
	
	 @GetMapping("/private/like/summary")
	    public String likeSummary() {
	        return "back/yiting/countStoreLike"; 
	 }
	
	
	
	
	@GetMapping("/public/front/findLikeByStoreMember")
	@ResponseBody
	public StoreLikeBean findByStoreMember(HttpSession session, @RequestParam("storeId") Integer storeId) {

	    if (session.getAttribute("loggedInMember") == null) {
	        return null;
	    } else {
	        MembersBean member = (MembersBean) session.getAttribute("loggedInMember");
	        StoresBean store = storeService.findStoreById(storeId);
	        StoreLikeBean byStoreMember = likeService.findByStoreMember(store, member);

	        if (byStoreMember == null) {
	            return null;
	        } else {
	            return byStoreMember;
	        }
	    }
	}
	
	@DeleteMapping("/public/front/like/delete")
	@ResponseBody
	public String deleteLikeByStoreAndMember(HttpSession session, @RequestParam("storeId") Integer storeId) {
		 if (session.getAttribute("loggedInMember") == null) {
		        return null;
		    } else {
		        MembersBean member = (MembersBean) session.getAttribute("loggedInMember");
		        StoresBean store = storeService.findStoreById(storeId);
		        likeService.deleteLikeByMemberAndStore(store, member);
		        return "success";
		    }
	}
	
	@GetMapping("/public/front/memberName")
	@ResponseBody
	public String insertLike(HttpSession session) {
	    if (session.getAttribute("loggedInMember") == null) {
	        return null;
	    } else {
	        MembersBean member = (MembersBean) session.getAttribute("loggedInMember");
	        return member.getMemberName();
	    }
	}
	
	@GetMapping("/public/front/personalLike")
	public String personalLike (HttpSession session, Model model) {
		
		List<ProductTypeNumberDto> countType= new ArrayList<>();
		
		LocalDate today = LocalDate.of(2024, 8, 2);
		
		List<StoresBean> checkInv = new ArrayList<>();  
		
		if (session.getAttribute("loggedInMember") == null) {
	        return null;
	    } else {
	        MembersBean member = (MembersBean) session.getAttribute("loggedInMember");
	        List<StoreLikeBean> personalLike = likeService.findByMember(member);
	        
	        for(StoreLikeBean like:personalLike) {
	        	List<String> types  = inventoryService.findProductType(like.getStore(), today);
	        	if(types.size() >= 1) {
	        		checkInv.add(like.getStore());
		        	for(String type:types) {
		        		ProductTypeNumberDto dto = new ProductTypeNumberDto();
		        		dto.setStoreId(like.getStore().getStoreId());
		        		dto.setType(type);
			        	List<InventoryBean> byTypeNoPage = inventoryService.findByTypeNoPage(like.getStore(), today,type);
			        	int count= 0;
			        	for(InventoryBean inv: byTypeNoPage) {
			        		count = count+inv.getInvNum();
			        	}
			        	dto.setNumber(count);
			        	countType.add(dto);
		        	}
	        	}
	        }
	       
	        model.addAttribute("checkInv", checkInv);
	        model.addAttribute("countType",countType);
	        model.addAttribute("personalLike",personalLike);
	        model.addAttribute("member", member);
	        return "/front/yiting/PersonalLike";
	    }
	}
	
	@GetMapping("/public/front/personalDelete")
	public String personalDelete(Integer likeId) {
		likeService.deleteStoreLike(likeId);
		return "redirect:/public/front/personalLike";
	}
	
	

}
