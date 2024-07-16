package six.sunny.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import six.hsiao.dto.ManagementDTO;
import six.hsiao.model.ManagementRoles;
import six.hsiao.model.ManagementRolesRepository;
import six.hsiao.model.MembersBean;
import six.hsiao.service.EmailService;
import six.pinhong.model.Product;
import six.pinhong.model.ProductRepository;
import six.pinhong.service.ProductService;
import six.sunny.model.GroupBuy;
import six.sunny.model.GroupMember;
import six.sunny.service.GroupBuyService;
import six.sunny.service.GroupMemberService;
import six.yiting.model.StoresBean;
import six.yiting.service.StoreService;

@Controller
public class GroupBuyController {
	
	@Autowired
	private GroupBuyService groupBuyService;
	
	@Autowired
	private GroupMemberService groupMemberService;

	@Autowired
	private ManagementRolesRepository managementRolesRepo;
	
//	TODO
	@Autowired
	private ProductRepository productService;
	
//	TODO
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private EmailService emailService;
	
	@RequestMapping(path = "private/back/GetAllGroupBuy", method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})
	public String getAllGroupBuy(Model m, HttpSession session) {
		
		
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();

		if (managementRole.getStore()!=null) {
			List<GroupBuy> gbs = groupBuyService.findByStoreId(managementRole.getStore().getStoreId());
			
			m.addAttribute("gbs", gbs);
			m.addAttribute("role", "店長");
		}else {
			List<GroupBuy> gbs = groupBuyService.findAll();	

//			右上角查詢下拉式選單用的store和product清單
			List<StoresBean> stns = storeService.findAllStores();
			List<Product> pdns = productService.findAll();
			
			m.addAttribute("gbs", gbs);
			m.addAttribute("stns", stns);
			m.addAttribute("pdns", pdns);
		}
		

		
		return "/back/sunny/GetAllGroupBuy";
	}
	
	@ResponseBody
	@GetMapping("private/back/FindByProductIdAndStoreId")
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
	
	@ResponseBody
	@GetMapping("private/back/FindByStatus")
	public List<GroupBuy> findByStatus(
			@RequestParam(value = "status", defaultValue = "") String status) {
		
		List<GroupBuy> gbs = null;
		
		if(status.isEmpty()) {
			gbs = groupBuyService.findAll();
		}else {
			gbs = groupBuyService.findByGroupBuyStatus(status);
		}
		
		return gbs;
		
	}
	
	@GetMapping("private/back/InsertGroupBuyForm")
	public String insertGroupBuyForm(Model m, HttpSession session) {
		
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		
//		取得表單填寫所需的List
		List<StoresBean> stns = new ArrayList<>();
		if (managementRole.getStore()!=null) {
			StoresBean stn = storeService.findStoreById(managementRole.getStore().getStoreId());
			stns.add(stn);
		}else {
			stns = storeService.findAllStores();
		}
		List<Product> pdns = productService.findAll();

		
		m.addAttribute("pdns", pdns);
		m.addAttribute("stns", stns);
		m.addAttribute("groupBuy", new GroupBuy());
		
		return "back/sunny/InsertGroupBuy";
	}
	
	@PostMapping("private/back/InsertGroupBuy")
	public String insertGroupBuy(@ModelAttribute GroupBuy groupBuy) {
		groupBuyService.insert(groupBuy);
		return "redirect:/private/back/GetAllGroupBuy";
	}
	
	@ResponseBody
	@DeleteMapping("private/back/DeleteGroupBuy")
	public ResponseEntity<Void> deleteGroupBuy(@RequestParam("id") Integer id, Model m) {
		boolean isDeleted = groupBuyService.deleteById(id);
	    if (isDeleted) {
	        return ResponseEntity.noContent().build();
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@GetMapping("private/back/UpdateGroupBuyForm")
	public String updateGroupBuyForm(@RequestParam("id") Integer groupBuyId, Model m, HttpSession session) {	
		
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();
		
//		取得表單填寫所需的List
		List<StoresBean> stns = new ArrayList<>();
		if (managementRole.getStore()!=null) {
			StoresBean stn = storeService.findStoreById(managementRole.getStore().getStoreId());
			stns.add(stn);
			m.addAttribute("role", "店長");
		}else {
			stns = storeService.findAllStores();
		}
		List<Product> pdns = productService.findAll();
		
		GroupBuy nowGb = groupBuyService.findById(groupBuyId);
		
		m.addAttribute("pdns", pdns);
		m.addAttribute("stns", stns);
		m.addAttribute("groupBuy", nowGb);

		return "back/sunny/UpdateGroupBuy";
	}
	
	@GetMapping("private/back/ChangeGroupBuyStatus")
	public String changeGroupBuyStatus(@RequestParam("id") Integer id,@RequestParam("status") String status, Model m) {		
		
		groupBuyService.changeGroupBuyStatus(id, status);
		
		if (status.equals("已到貨")) {
			emailService.sendGroupBuyEmail(id);
		}
		
		return "redirect:/private/back/GetAllGroupBuy";
	}
	
	@PutMapping("private/back/UpdateGroupBuy")
	public String updateGroupBuy(@ModelAttribute("GroupBuyBean") GroupBuy groupBuyBean) {


		
//		更新
		groupBuyService.update(groupBuyBean);

		return "redirect:/private/back/GetAllGroupBuy";
	}
	
	@GetMapping("public/front/group-buys")
	public String GroupBuys(@RequestParam(value = "p",defaultValue = "1") Integer page,Model m) {
		
		Page<GroupBuy> gbs = groupBuyService.findByGroupBuyStatus("開團中",page);
		m.addAttribute("gbs", gbs);
		
		return "front/sunny/GroupBuy";
	}
	
	@GetMapping("public/front/group-buys/{id}")
	public String getOneGroupBuy(@PathVariable("id") Integer id, HttpSession session, Model m) {
		if (session.getAttribute("loggedInMember") == null) {
			return "redirect:/public/frontLoginMain";
		}
		
		GroupBuy groupBuy = groupBuyService.findById(id);
		
		m.addAttribute("gb", groupBuy);
		
		return "front/sunny/GroupBuyDetail";
	}
	
	@GetMapping("public/front/group-buy-orders")
	public String getGroupBuyOrders(HttpSession session, Model m) {
		if (session.getAttribute("loggedInMember") == null) {
			return "redirect:/public/frontLoginMain";
		}

		MembersBean member = (MembersBean) session.getAttribute("loggedInMember");
		List<GroupMember> list = groupMemberService.findByMemberId(member.getMemberId());
		
		m.addAttribute("gms",list);
		
		return "front/sunny/GroupBuyOrder";
	}
	
}
