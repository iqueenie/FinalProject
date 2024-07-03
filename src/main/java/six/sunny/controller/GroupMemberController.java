package six.sunny.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import six.hsiao.model.MembersBean;
import six.sunny.model.GroupMember;
import six.sunny.service.GroupMemberService;

@Controller
public class GroupMemberController {
	
	@Autowired
	private GroupMemberService groupMemberService;
	
//	@Autowired
//	private IGroupBuyService groupBuyService;
//	
//	@Autowired
//	private MembersService membersService;
//	
	@RequestMapping(path = "/GetGroupMemberByGroupBuy", method = {RequestMethod.GET,RequestMethod.POST})
	public String getGroupMemberByGroupBuy(@RequestParam("gbId") Integer gbId, Model m) {
		
//		透過GroupBuy取得GroupMember
		List<GroupMember> gms = groupMemberService.findByGroupBuyId(gbId);
		
		m.addAttribute("gbId", gbId);
		m.addAttribute("gms", gms);

		return "back/sunny/GetGroupMemberByGroupBuy";
	}
	
//	@GetMapping("InsertGroupMemberForm")
//	public String insertGroupMemberForm(@RequestParam("id") Integer id, Model m) {
//		
////		取得下拉式選單所需的列表
//		List<MembersBean> mbns = membersService.findAll();
//		GroupMemberBean groupMemberBean = new GroupMemberBean();
//		groupMemberBean.setGroupBuyId(id);
//		
//		m.addAttribute("GroupMemberBean", groupMemberBean);
//		m.addAttribute("mbns", mbns);
//		
//		return "sunny/InsertGroupMember";
//	}
	
//	@PostMapping("InsertGroupMember")
//	public String insertGroupMember(@ModelAttribute("GroupMemberBean") GroupMemberBean gm) {
//		
//		
//		gm.setGroupBuy(groupBuyService.findById(gm.getGroupBuyId()));
//		gm.setMember(membersService.findById(gm.getMemberId()));
//		gm.setPickupStatus("已訂購");
//		
////		新增
//		groupMemberService.insert(gm);
//
//		return "forward:/GetGroupMemberByGroupBuy?gbId="+gm.getGroupBuyId();
//	}
//	
//	@GetMapping("DeleteGroupMember")
//	public String deleteGroupMember(@RequestParam("gmId") Integer groupMemberId, @RequestParam("gbId") Integer groupBuyId) {
//
////		刪除
//		groupMemberService.deleteById(groupMemberId);
//
//		return "forward:/GetGroupMemberByGroupBuy?gbId="+groupBuyId;
//	}
//	
//	@GetMapping("UpdateGroupMemberForm")
//	public String updateGroupMemberForm(@RequestParam("gmId") Integer groupMemberId,@RequestParam("gbId") Integer groupBuyId, Model m) {
////		TODO:delete gbId
////		取得表單使用內容
//	    List<MembersBean> mbns = membersService.findAll();
//	    GroupMemberBean groupMemberBean = groupMemberService.findById(groupMemberId);
//
//	    m.addAttribute("mbns", mbns);
//	    m.addAttribute("GroupMemberBean", groupMemberBean);
//	    return "sunny/UpdateGroupMember";
//
//	}
//	
//	@PostMapping("UpdateGroupMember")
//	public String updateGroupMember(@ModelAttribute("GroupMemberBean") GroupMemberBean gm,Model m) {
//		
//		
//		String pickupStatus = gm.getPickupStatus();
//		gm.setMember(membersService.findById(gm.getMemberId()));
//		gm.setGroupBuy(groupBuyService.findById(gm.getGroupBuyId()));
//		
//		GroupMemberBean newGroupMember = groupMemberService.update(gm);
//		
////		如果狀態調整後有不同則設一個變數
//		if(!pickupStatus.equals(newGroupMember.getPickupStatus())) {
//			m.addAttribute("pus", newGroupMember.getPickupStatus());
//		}
//
//		return "forward:/GetGroupMemberByGroupBuy?gbId="+gm.getGroupBuyId();
//	}
}
