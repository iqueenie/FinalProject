package six.sunny.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import six.hsiao.model.MembersBean;
import six.sunny.model.GroupMember;
import six.sunny.model.MemberNameRepository;
import six.sunny.service.GroupBuyService;
import six.sunny.service.GroupMemberService;

@Controller
public class GroupMemberController {
	
	@Autowired
	private GroupMemberService groupMemberService;
	
	@Autowired
	private GroupBuyService groupBuyService;
	
	@Autowired
	private MemberNameRepository membersService;
	
	@GetMapping("private/GetGroupMemberByGroupBuy")
	public String getGroupMemberByGroupBuy(@RequestParam("gbId") Integer gbId, @RequestParam(value = "pus", defaultValue = "null") String pus, Model m) {
		
//		透過GroupBuy取得GroupMember
		List<GroupMember> gms = groupMemberService.findByGroupBuyId(gbId);
		
		if (!pus.equals("null")) {
			int pus2 = Integer.parseInt(pus);
			String status = groupMemberService.findById(pus2).getPickupStatus();
			m.addAttribute("pus", status);
		}
		m.addAttribute("gbId", gbId);
		m.addAttribute("gms", gms);

		return "back/sunny/GetGroupMemberByGroupBuy";
	}
	
	@GetMapping("private/InsertGroupMemberForm")
	public String insertGroupMemberForm(@RequestParam("id") Integer id, Model m) {
		
//		取得下拉式選單所需的列表
		List<MembersBean> mbns = membersService.findAll();
		GroupMember groupMember = new GroupMember();
		groupMember.setGroupBuyId(id);
		
		m.addAttribute("groupMember", groupMember);
		m.addAttribute("mbns", mbns);
		
		return "back/sunny/InsertGroupMember";
	}
	
	@PostMapping("private/InsertGroupMember")
	public String insertGroupMember(@ModelAttribute GroupMember gm) {
		
//		新增
		groupMemberService.insert(gm);

		return "redirect:/private/GetGroupMemberByGroupBuy?gbId="+gm.getGroupBuyId();
	}
	
	@DeleteMapping("private/DeleteGroupMember")
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
	
	@GetMapping("private/UpdateGroupMemberForm")
	public String updateGroupMemberForm(@RequestParam("gmId") Integer groupMemberId, Model m) {

//		取得表單使用內容
	    List<MembersBean> mbns = membersService.findAll();
	    GroupMember groupMember = groupMemberService.findById(groupMemberId);

	    m.addAttribute("mbns", mbns);
	    m.addAttribute("groupMember", groupMember);
	    return "back/sunny/UpdateGroupMember";

	}
	
	@PutMapping("private/UpdateGroupMember")
	public String updateGroupMember(@ModelAttribute GroupMember gm) {
		
		
		String pickupStatus = gm.getPickupStatus();
		
		GroupMember newGroupMember = groupMemberService.update(gm);
		
//		如果狀態調整後有不同則設一個變數
		Integer pus = null;
		if(!pickupStatus.equals(newGroupMember.getPickupStatus())) {
			pus = newGroupMember.getGroupMemberId();
		}

		return "redirect:/private/GetGroupMemberByGroupBuy?gbId="+gm.getGroupBuyId()+"&pus="+pus;
	}
}
