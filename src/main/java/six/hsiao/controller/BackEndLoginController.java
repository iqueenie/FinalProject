package six.hsiao.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import six.hsiao.service.BackEndLoginService;

@Controller
public class BackEndLoginController {
	
	@Autowired
	BackEndLoginService backEndLoginService;
	
	
	//登入進入點
	 @GetMapping("/BackLoginMain")
	public String goToBackLogin() {
		 return "back/hsiao/BackLoginAction";
	}
	//登入
	@PostMapping("/BackLogin")
	public String BackLogin(@RequestParam("managementAccountt")String managementAccount,@RequestParam("managementPassword")String managementPassword,HttpSession session,Model model ) {
		 boolean isValidUser = backEndLoginService.findBymanagement(managementAccount, managementPassword);
	 
		 if(isValidUser) {
			 session.setAttribute("managementAccount", managementAccount);
			    return "all/index"; 
        } else {
	        	model.addAttribute("BackLogin","帳號密碼有誤,請重新輸入");
	          
	            return "back/hsiao/BackLoginMain"; 
        }
	    }


	//登出
	@GetMapping("/BackLogout")
	public String BackLogout(HttpSession session,Model model) {
		session.invalidate();
       model.addAttribute("logout", "您已成功登出！");
		return "back/hsiao/BackLoginAction";
	}

	
	}
 
	 


