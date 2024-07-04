package six.hsiao.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;



@Controller
public class MembersController {
	
	@Autowired
	private MembersService membersService;
	
	//查詢整張表
	@GetMapping("/GetAllMembers")
	public String getAllProudcts(Model model) {
		List<MembersBean> members = membersService.findAll();
		model.addAttribute("members", members);
		return "back/hsiao/GetAllMembers";
	}
	
	
	 //新增進入點 (表單)
	 @GetMapping("/insertMembersMain")
	 public String insertMain() {
		 
		 return "back/hsiao/insertMember";
	 }
	
	 //新增
	 @PostMapping("/insertMembers")
	 public  String insertMembers(@ModelAttribute MembersBean member,
				@RequestParam("memberPhotoFile") MultipartFile memberPhotoFile,Model model){
		
		 if(!memberPhotoFile.isEmpty()) {
			 try {
				byte[] memberPhoto = memberPhotoFile.getBytes();
				member.setMemberPhoto(memberPhoto);
			} catch (IOException e) {
				
				model.addAttribute("errorByPhoto","上傳圖片失敗,請檢查檔案大小");	
				e.printStackTrace();
			}
			  membersService.insertMembers(member);
			
		 }
		return "redirect:/GetAllMembers";
		 
		 
	 }
	 
	 
	 //更新進入點(表單)
	 @GetMapping("/updateMembersMain")
	 public String updateMain() {
		 return "back/hsiao/updateMember";
	 }
	 
	 
	 //更新
	@PostMapping("/updateMembers")
	public String updateMember(@ModelAttribute("member") MembersBean member,
            @RequestParam(value = "memberPhotoFile", required = false) MultipartFile memberPhotoFile,
            Model model) {
		if (memberPhotoFile != null && !memberPhotoFile.isEmpty()) {
			
			try {
				byte[]	memberPhoto = memberPhotoFile.getBytes();
				member.setMemberPhoto(memberPhoto);
			} catch (IOException e) {
				model.addAttribute("errorByPhoto","上傳圖片失敗,請檢查檔案大小");	
				e.printStackTrace();
			}
			
			membersService.insertMembers(member);
		}
		return "redirect:/GetAllMembers";
	}
	 
	 
	//刪除
	@PostMapping("/deleteMembers")
	public String deleteMember(@RequestParam("membersId") Integer membersId) {
		membersService.deleteById(membersId);
		return "redirect:/GetAllMembers";
	}
	
	
}
