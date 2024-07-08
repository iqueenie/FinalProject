package six.yiting.controller;

import java.util.ArrayList;
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

import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;
import six.pinhong.model.Product;
import six.yiting.model.BuyBean;
import six.yiting.model.DetailBean;
import six.yiting.model.StoreLikeBean;
import six.yiting.model.StoresBean;
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
	
	
	@GetMapping("/like/findAll")
	public String findAllLikes(Model model) {
		
		List<StoreLikeBean> listLikes = likeService.findAllLikes();
		
		model.addAttribute("listLikes",listLikes);
		
		
		return "back/yiting/GetAllStoreLikes";
	}
	
	@GetMapping("/like/findAllAjax")
	@ResponseBody
	public List<StoreLikeBean> findAllStoresAjax() {
		
		return likeService.findAllLikes();
		
	}
	
	@GetMapping("/like/addLikePage")
    public String storeInsertPage(Model m) {
		List<StoresBean> stores = storeService.findAllStores();
		List<MembersBean> members = membersService.findAll();
		m.addAttribute("stores", stores);
		m.addAttribute("members", members);
		m.addAttribute("like", new StoreLikeBean());
		return "back/yiting/insertLike";
		
	}
	
	@PostMapping("/like/insertLike")
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
			
			return "redirect:/like/findAll";
		}
		
	}
	
	
	@DeleteMapping("/like/delete")
	@ResponseBody
	public String deleteStore(@RequestParam Integer id) {
	    likeService.deleteStoreLike(id);
	    return "success"; 
	}
	
	@GetMapping("/like/edit")
	public String editLike(@RequestParam Integer id, Model model) {
		
		StoreLikeBean like = likeService.findLikeById(id);
		List<StoresBean> stores = storeService.findAllStores();
		List<MembersBean> members = membersService.findAll();
		model.addAttribute("stores", stores);
		model.addAttribute("members", members);
		model.addAttribute("like", like);
		
		return "back/yiting/editLike";
	}
	
	@PutMapping("/like/editPost")
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
			
			return "redirect:/like/findAll";
		}
	}
	
	@GetMapping("/like/count")
	public String countLike(Model m) {
		
		List<StoresBean> listStores = storeService.findAllStores();
		List<StoresBean> storeCount = new ArrayList<StoresBean>();
		List<Long> likeCount = new ArrayList<Long>();
		
		for(StoresBean store: listStores) {
			long likeNum = likeService.countLikes(store);
			if (likeNum>0) {
				storeCount.add(store);
				likeCount.add(likeNum);
			}
		}
		m.addAttribute("storeCount", storeCount);
		m.addAttribute("likeCount", likeCount);
		
		
		return "back/yiting/countStoreLike";
	}

}
