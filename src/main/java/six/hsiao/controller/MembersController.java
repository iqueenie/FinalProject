package six.hsiao.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;



@Controller
public class MembersController {
	
	@Autowired
	private MembersService membersService;
	
	//查詢整張表
	@GetMapping("/private/GetAllMembers")
	public String getAllProudcts(Model model) {
		List<MembersBean> members = membersService.findAll();
		model.addAttribute("members", members);
		return "back/hsiao/GetAllMembers";
	}
	
	
	@GetMapping("/private/MemberPhoto")
	public ResponseEntity<byte[]> getMemberPhoto(@RequestParam Integer memberId) {
	   
	    MembersBean member = membersService.findByMemberId(memberId);
	    
	    if (member == null || member.getMemberPhoto() == null) {
	        // 如果找不到會員或者會員圖片為空，返回 404 狀態碼
	        return ResponseEntity.notFound().build();
	    }
	    
	    byte[] memberPhoto = member.getMemberPhoto(); //儲存在 memberPhoto 屬性中
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_JPEG); // 設置 Content-Type 為圖片類型，JPEG 圖片
	    
	    return ResponseEntity.ok().headers(headers).body(memberPhoto);
	}
	
	
	 //新增進入點 (表單)
	 @GetMapping("/private/insertMembersMain")
	 public String insertMain() {
		 
		 return "back/hsiao/InsertMember";
	 }
	
	 //新增
	 @PostMapping("/private/insertMembers")
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
		return "redirect:/private/GetAllMembers";
		 
		 
	 }
	 
	 
	 //更新進入點(表單)
	 @GetMapping("/private/updateMembersMain/{memberId}")
	 public String updateMain(@PathVariable Integer memberId, Model model) {
	     MembersBean member = membersService.findByMemberId(memberId);
	     model.addAttribute("member", member);
	     return "back/hsiao/UpdateMember";
	 }
	 
	 
	 //更新
	 @PostMapping("/private/updateMembers/{memberId}")
	 public String updateMember(@ModelAttribute("member") MembersBean member,
	                            @RequestParam(value = "memberPhotoFile", required = false) MultipartFile memberPhotoFile,
	                            @RequestParam("memberId") Integer memberId,
	                            Model model) {
	    

	     if (memberPhotoFile != null && !memberPhotoFile.isEmpty()) {
	         try {
	             byte[] memberPhoto = memberPhotoFile.getBytes();
	             member.setMemberPhoto(memberPhoto);
	         } catch (IOException e) {
	             model.addAttribute("errorByPhoto", "上傳圖片失敗,請檢查檔案大小");
	             e.printStackTrace();
	         }
	     }

	     // 更新
	     membersService.updateMembers(member);

	     return "redirect:/private/GetAllMembers";
	 }
	 
	 
	 @DeleteMapping("/private/deleteMember")
	 public ResponseEntity<String> deleteMember(@RequestParam Integer id) {
	     try {
	         membersService.deleteById(id);
	         return ResponseEntity.ok("success");
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
	     }
	 }
	 
	 }
	
	