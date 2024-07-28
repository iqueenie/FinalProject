package six.hsiao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import six.hsiao.dto.ManagementDTO;
import six.hsiao.model.Message;
import six.hsiao.service.ManagementService;
import six.hsiao.service.MessageService;


@Controller
public class ManagementController {
	
	
	 @Autowired
	 private ManagementService managementService;
	 
	 @Autowired
	 private  MessageService messageService;
	 
	 
	@GetMapping("/public/BackLoginMain")
	public String BackLoginMain() {
		return "back/hsiao/BackLoginAction";
	}
	 
	
	 @PostMapping("/public/login")
	    public String loginManagement(@RequestParam("managementAccount") String managementAccount,
	                                  @RequestParam("managementPassword") String managementPassword,
	                                  HttpSession session) {

	        ManagementDTO managementDTO = managementService.findManagementDTO(managementAccount, managementPassword);

	        if (managementDTO != null) {
	            // 登入成功，將管理員和角色信息存入 session 中
	            session.setAttribute("logInManagement", managementDTO);
	            return "redirect:/private/back"; 
	        } else {
	            return "redirect:/public/BackLoginMain";
	        }
	    }
	 
	 
	 
	 @GetMapping("/public/logout")
	 public String logout(HttpSession session) {
	     // 只刪除 session 中的管理員信息,但會保留登入信息
	     session.removeAttribute("logInManagement");
	     
	     // 下面的方法 可以讓當前使用者的session失效,完全清除session中的所有數據
	     // session.invalidate();
	     
	     
	     return "redirect:/public/BackLoginMain";
	 }
		
	 
	 
	
	@GetMapping("/addManagementMain")
	public String addManagementMain() {
	    return "back/hsiao/addManagement";
	}
	
	
	
	
	
	@PostMapping("/addManagement")
	public String addManagement(@RequestParam("managementAccount") String managementAccount,
            @RequestParam("managementPassword") String managementPassword,Model model) {
		
		try {
            managementService.addManagement(managementAccount, managementPassword);
//            model.addAttribute("addManagement","新增成功");
           
        } catch (Exception e) {
            e.printStackTrace();
//          model.addAttribute("error","請確認格式,請用數字或者是英文組成的帳號密碼");
           
        }
		 return "redirect:/BackLoginMain";  
		
	}
	
	
}
