package six.hsiao.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import six.hsiao.model.MemberEmailRepository;
import six.hsiao.model.MembersBean;
import six.hsiao.service.EmailService;

@Controller
public class MemberEmailController {
	
	@Autowired
	private EmailService emailService ;
	
	
	@Autowired
    private MemberEmailRepository memberEmailRepository;
	
	
	
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
		 
        
		  try {
			  emailService.sendResetPasswordEmail(memberEmail, token);
	      
			  redirectAttributes.addAttribute("success", "已發送郵件至：" + memberEmail);
			 
	        } catch (Exception e) {
	        	 redirectAttributes.addAttribute("error", "發送郵件失敗：" + e.getMessage());
	        }
		  return "redirect:/public/ForgetThePasswordMain";
	    }
	

	
	}

	
	
	

