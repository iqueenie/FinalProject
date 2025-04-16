package six.sunny.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpSession;
import six.hsiao.dto.ManagementDTO;
import six.hsiao.model.ManagementRoles;
import six.hsiao.model.ManagementRolesRepository;
import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;
import six.sunny.model.GroupBuy;
import six.sunny.model.GroupMember;
import six.sunny.model.GroupMemberDTO;
import six.sunny.service.GroupBuyService;
import six.sunny.service.GroupMemberService;

@Controller
public class GroupMemberController {
	
	@Autowired
	private GroupMemberService groupMemberService;
	
	@Autowired
	private MembersService membersService;
	
	@Autowired
	private ManagementRolesRepository managementRolesRepo;
	
	@GetMapping("private/back/GetGroupMemberByGroupBuy")
	public String getGroupMemberByGroupBuy(@RequestParam("gbId") Integer gbId, @RequestParam(value = "pus", defaultValue = "null") String pus, Model m, HttpSession session) {
		
//		透過GroupBuy取得GroupMember
		List<GroupMember> gms = groupMemberService.findByGroupBuyId(gbId);
		
		if (!pus.equals("null")) {
			int pus2 = Integer.parseInt(pus);
			String status = groupMemberService.findById(pus2).getPickupStatus();
			m.addAttribute("pus", status);
		}
		
		ManagementDTO managementDTO = (ManagementDTO)session.getAttribute("logInManagement");
		ManagementRoles managementRole = managementRolesRepo.findById(managementDTO.getManagementId()).get();

		if (managementRole.getStore()!=null) {
			m.addAttribute("role", "店長");			
		}
		
		m.addAttribute("gbId", gbId);
		m.addAttribute("gms", gms);

		return "back/sunny/GetGroupMemberByGroupBuy";
	}
	
	@GetMapping("private/back/InsertGroupMemberForm")
	public String insertGroupMemberForm(@RequestParam("id") Integer id, Model m) {
		
//		取得下拉式選單所需的列表
		List<MembersBean> mbns = membersService.findAll();
		GroupMember groupMember = new GroupMember();
		groupMember.setGroupBuyId(id);
		
		m.addAttribute("groupMember", groupMember);
		m.addAttribute("mbns", mbns);
		
		return "back/sunny/InsertGroupMember";
	}
	
	@PostMapping("private/back/InsertGroupMember")
	public String insertGroupMember(@ModelAttribute GroupMember gm) {
		
//		新增
		groupMemberService.insert(gm);

		return "redirect:/private/back/GetGroupMemberByGroupBuy?gbId="+gm.getGroupBuyId();
	}
	
	@DeleteMapping("private/back/DeleteGroupMember")
	@ResponseBody
	public ResponseEntity<Void> deleteGroupMember(@RequestParam("id") Integer groupMemberId) {
		
//		刪除
		boolean isDeleted = groupMemberService.deleteById(groupMemberId);

	    if (isDeleted) {
	        return ResponseEntity.noContent().build();
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@GetMapping("private/back/UpdateGroupMemberForm")
	public String updateGroupMemberForm(@RequestParam("gmId") Integer groupMemberId, Model m) {

//		取得表單使用內容
	    List<MembersBean> mbns = membersService.findAll();
	    GroupMember groupMember = groupMemberService.findById(groupMemberId);

	    m.addAttribute("mbns", mbns);
	    m.addAttribute("groupMember", groupMember);
	    return "back/sunny/UpdateGroupMember";

	}
	
	@PutMapping("private/back/UpdateGroupMember")
	public String updateGroupMember(@ModelAttribute GroupMember gm) {
		
		
		String pickupStatus = gm.getPickupStatus();
		
		GroupMember newGroupMember = groupMemberService.update(gm);
		
//		如果狀態調整後有不同則設一個變數
		Integer pus = null;
		if(!pickupStatus.equals(newGroupMember.getPickupStatus())) {
			pus = newGroupMember.getGroupMemberId();
		}

		return "redirect:/private/back/GetGroupMemberByGroupBuy?gbId="+gm.getGroupBuyId()+"&pus="+pus;
	}
	
	@ResponseBody
	@PostMapping("public/front/group-members")
	public ResponseEntity<Void> orderGroupBuy(@RequestBody GroupMemberDTO groupMemberDTO) {
		
		GroupMember gm = new GroupMember();
		gm.setGroupBuyId(groupMemberDTO.getGroupBuyId());
		gm.setMemberId(groupMemberDTO.getMemberId());
		gm.setQuantity(groupMemberDTO.getQuantity());
		
		groupMemberService.insert(gm);
		
	    URI location = null;
	    try {
	    	location = new URI("http://localhost:8080/FinalProject/public/front/group-member-orders");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return ResponseEntity.created(location).build();
	}
	
	@ResponseBody
	@PutMapping("public/front/group-members/{id}")
	public ResponseEntity<GroupMember> deleteGroupBuyOrder(@PathVariable("id") Integer groupMemberId) {
	    GroupMember delete = groupMemberService.updateStatusToDelete(groupMemberId);
	    
	    if (delete != null) {
	        return ResponseEntity.ok(delete);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@ResponseBody
	@GetMapping("public/front/group-member-quantity")
	public ResponseEntity<GroupMember> updateGroupBuyOrder(
			@RequestParam("id") Integer groupMemberId,
			@RequestParam("quantity") Integer quantity) {
		
		GroupMember updateQuantity = groupMemberService.updateQuantity(groupMemberId, quantity);
		
		return ResponseEntity.ok(updateQuantity);
	}
	
	@GetMapping("private/back/ChangePickupStatus")
	public String changeGroupBuyStatus(@RequestParam("id") Integer id,@RequestParam("status") String status) {		
		
		groupMemberService.changeGroupMemberStatus(id, status);
		
		return "redirect:/private/back/GetGroupMemberByGroupBuy?gbId="+groupMemberService.findById(id).getGroupBuyId();
	}
	
	@GetMapping("public/front/group-member-orders")
	public String getGroupBuyOrders(HttpSession session, Model m) {
		if (session.getAttribute("loggedInMember") == null) {
			return "redirect:/public/frontLoginMain";
		}

		MembersBean member = (MembersBean) session.getAttribute("loggedInMember");
		Page<GroupMember> list = groupMemberService.findByMemberId(member.getMemberId(), 1);
		
		m.addAttribute("gms",list);
		
		return "front/sunny/GroupBuyOrder";
	}
	
	@ResponseBody
	@GetMapping("public/front/group-member-orders-all")
	public Page<GroupMember> getGroupBuyOrdersPage(HttpSession session, Model m, @RequestParam("p") Integer page) {
		if (session.getAttribute("loggedInMember") != null) {
			MembersBean member = (MembersBean) session.getAttribute("loggedInMember");
			Page<GroupMember> list = groupMemberService.findByMemberId(member.getMemberId(), page);
			
			return list;
		}
		return null;
	}
	
	@ResponseBody
	@GetMapping("public/front/group-member-orders-by-status")
	public Page<GroupMember> getGroupBuyOrdersByStatus(HttpSession session, Model m,
			@RequestParam("p") Integer page,
			@RequestParam(value = "status", defaultValue = "all") String status) {
		if (session.getAttribute("loggedInMember") != null) {
			MembersBean member = (MembersBean) session.getAttribute("loggedInMember");
			Page<GroupMember> list = null;
			if (status.equals("all")) {
				list = groupMemberService.findByMemberId(member.getMemberId(), page);
			}else {
				list = groupMemberService.findByMemberIdAndPickupStatus(member.getMemberId(), status, page);
			}
			return list;
		}
		return null;
	}
}
