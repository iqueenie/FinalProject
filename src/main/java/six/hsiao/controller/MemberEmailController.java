package six.hsiao.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import six.hsiao.model.MemberEmailRepository;
import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.hsiao.service.EmailService;

@Controller
public class MemberEmailController {
	
	@Autowired
	private EmailService emailService ;
	
	
	@Autowired
    private MemberEmailRepository memberEmailRepository;
	
	@Autowired
	private MembersRepository membersRepository;
	
	
	@GetMapping("/public/ForgetThePasswordMain")
	 public String ForgetThePasswordMain() {
		 return "front/hsiao/ForgetThePassword";
	 }
	 
	
	

	@PostMapping("/front/ForgetThePassword")
	public String forgotPassword(@RequestParam("memberEmail") String memberEmail,RedirectAttributes redirectAttributes){
		 MembersBean member = memberEmailRepository.findBymemberEmail(memberEmail);
		 
		 if(member == null) {
			   redirectAttributes.addAttribute("error", "找不到註冊信箱：" + memberEmail);
			   return "redirect:/public/ForgetThePasswordMain";
		 }
		 //隨機的token 每次用戶取得token都會不一樣,確保認證的安全性
		 String token = UUID.randomUUID().toString().replaceAll("-", "");
		 member.setResetToken(token);
         memberEmailRepository.save(member);
        
		  try {
			  emailService.sendResetPasswordEmail(memberEmail, token);
	      
			  redirectAttributes.addAttribute("success", "已發送郵件至：" + memberEmail);
			 
	        } catch (Exception e) {
	        	 redirectAttributes.addAttribute("error", "發送郵件失敗：" + e.getMessage());
	        }
		  return "redirect:/public/ForgetThePasswordMain";
	    }
	
		
	
	@GetMapping("/public/ResetPasswordMain")
		public String ResetPasswordMain(@RequestParam("token")String token,Model model) {
			MembersBean member = memberEmailRepository.findByResetToken(token);
			if(member == null) {
				model.addAttribute("error","無效的連結,請重新認證");
				return "redirect:/public/ForgetThePasswordMain";
			}
			model.addAttribute("token",token);
			return "front/hsiao/ResetPassword";
		
	}
	
	@PostMapping("/front/ForgetThePasswordUpdate")
	public String ResetPassword(@RequestParam("token")String token,@RequestParam("memberPassword")String memberPassword,@RequestParam("confirmPassword")String confirmPassword,RedirectAttributes redirectAttributes) {
		 MembersBean member = memberEmailRepository.findByResetToken(token);
		 if(!memberPassword.equals(confirmPassword)) {
			 redirectAttributes.addAttribute("error","密碼不一致,請重新輸入");
			 return "redirect:/public/ForgetThePasswordMain";
		 }
		 
		if(member == null) {
			redirectAttributes.addAttribute("error","無效的連結,請重新認證");
		 return "redirect:/public/ForgetThePasswordMain";
		}
		
		 member.setMemberPassword(memberPassword);
		//這裡更新會員後 更新token 讓他變成空的
		 member.setResetToken(null);
		 
		 membersRepository.save(member);
	
		 redirectAttributes.addAttribute("success","密碼重置成功 請重新登入");
		 return "redirect:/public/ForgetThePasswordMain";
	}
	 
		
		
	}

	
	
	

