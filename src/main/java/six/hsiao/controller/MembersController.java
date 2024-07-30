package six.hsiao.controller;

import java.io.IOException;
import java.io.InputStream;
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

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.hsiao.service.EmailService;
import six.hsiao.service.MembersService;



@Controller
public class MembersController {
	
	@Autowired
    private HttpServletRequest request;
	
	
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
	 
	 @GetMapping("/public/frontLoginMain")
		public String frontLoginMain() {
			return "front/hsiao/FrontLoginAction";
		}
		 
	 
	 
	 @PostMapping("/front/login")
	 public String loginMember(@RequestParam("memberAccount")String memberAccount ,@RequestParam("memberPassword")String memberPassword,HttpSession session,RedirectAttributes redirectAttributes) {
		 MembersBean member = membersService.findByMemberAccountAndMemberPassword(memberAccount, memberPassword);
		 
		 
		 
		 if(member !=null) {
			 if ("封鎖".equals(member.getLockStatus())) {
				 redirectAttributes.addAttribute("error","你的帳戶已被封鎖，無法登入");
				 return "redirect:/public/frontLoginMain";
			 }
			 session.setAttribute("loggedInMember", member);
			 return "redirect:/public/front";
			 
		 }
		 redirectAttributes.addAttribute("error","帳號密碼錯誤請重新登入");
		 return "redirect:/public/frontLoginMain";
		
	 }
	 
	 
	 @GetMapping("/front/logout")
	 public String logout(HttpSession session) {
	     // 只刪除 session 中的管理員信息,但會保留登入信息
		 	session.removeAttribute("loggedInMember");
	     
	     //下面的方法 可以讓當前使用者的session失效,完全清除session中的所有數據
//	     session.invalidate();
	     
	     
	     return "redirect:/public/front";
	 }
	 
	 @ResponseBody
	 @GetMapping("/front/checkLogging")
	 public boolean getMethodName(HttpSession session) {
		if (session.getAttribute("loggedInMember") == null) {
			return false;
		}else {
			return true;
		}
	 }
	 
	 @GetMapping("/public/addMemberMain")
	 public String addMemberMain() {
		 return "front/hsiao/AddMember";
	 }
	 
	 
	 @GetMapping("/public/addMember")
	 public String addMember(@ModelAttribute MembersBean member,
				@RequestParam("memberPhotoFile") MultipartFile memberPhotoFile,Model model) {
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
		return "redirect:/public/frontLoginMain";
		 
	 }
	 
		@GetMapping("/MemberPhoto")
		public ResponseEntity<byte[]> getMemberPhotoForFront(@RequestParam Integer memberId) {
		   
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
		
		
	 

		 @PostMapping("/front/insertMembers")
		 public  String frontString (@ModelAttribute MembersBean member,
					@RequestParam("memberPhotoFile") MultipartFile memberPhotoFile,Model model){
			
			 if(!memberPhotoFile.isEmpty()) {
				 try {
					byte[] memberPhoto = memberPhotoFile.getBytes();
					member.setMemberPhoto(memberPhoto);
					member.setPoints(0);
				} catch (IOException e) {
					
					model.addAttribute("errorByPhoto","上傳圖片失敗,請檢查檔案大小");	
					e.printStackTrace();
				}
				  membersService.insertMembers(member);
				
			 }
			return "redirect:/public/frontLoginMain";
			 
			 
		 }
		 
	 
		 @PostMapping("/private/updateLockStatus")
		 public String toggleLockStatus(@RequestParam Integer memberId, RedirectAttributes redirectAttributes) {
		     try {
		         MembersBean member = membersService.findByMemberId(memberId);
		         if (member != null) {
		             String Status = member.getLockStatus().equals("正常") ? "封鎖" : "正常";
		             member.setLockStatus(Status);
		             membersService.insertMembers(member);
		             redirectAttributes.addFlashAttribute("message", "會員狀態已更新");
		         } else {
		             redirectAttributes.addFlashAttribute("error", "找不到該會員");
		         }
		     } catch (Exception e) {
		         redirectAttributes.addFlashAttribute("error", "無法更新狀態");
		     }
		     return "redirect:/private/GetAllMembers"; 
		 }
		 
		 
		 
		 @GetMapping("/public/MemberProfileMain")
		 public String memberProfileMain( HttpSession httpSession,Model model) {
	
			Object members = httpSession.getAttribute("loggedInMember");
			
			
			model.addAttribute("loggedInMember", members);
			 return "front/hsiao/MemberProfile";
		 }
		 
		 
		 
		 
		 		@PostMapping("/front/UpdeMember")
		 		public String frontUpdeMember(@ModelAttribute MembersBean member,
		                                  @RequestParam(value = "memberPhotoFile", required = false) MultipartFile memberPhotoFile,
		                                  Model model) {

		      
		        HttpSession session = request.getSession();
		        MembersBean loggedInMember = (MembersBean) session.getAttribute("loggedInMember");
		        Integer members = loggedInMember.getMemberId();
		        MembersBean byMember = membersService.findByMemberId(members);
		      
		        if (byMember !=null) {
		           
		        	byMember.setMemberName(member.getMemberName());
		        	byMember.setMemberAccount(member.getMemberAccount());
		        	byMember.setMemberPassword(member.getMemberPassword());
		        	byMember.setMemberAddress(member.getMemberAddress());
		        	byMember.setMemberBirthDate(member.getMemberBirthDate());
		        	byMember.setMemberEmail(member.getMemberEmail());
		        	
		        	session.setAttribute("loggedInMember", byMember);
		        	
		        

		          
		            if (memberPhotoFile != null && !memberPhotoFile.isEmpty()) {
		                try {
		                    byte[] newMemberPhoto = memberPhotoFile.getBytes();
		                    byMember.setMemberPhoto(newMemberPhoto);
		                } catch (Exception e) {
		                    model.addAttribute("errorByPhoto", "上傳圖片失敗,請檢查檔案大小");
		                    e.printStackTrace();
		                }
		            }

		            membersService.updateMembers(byMember);

		            return "redirect:/public/MemberProfileMain";
		        } else {
		            
		            return "redirect:public/front";
		        }
		    }
		



}
		 
		 
		
	

	 
	 
	

