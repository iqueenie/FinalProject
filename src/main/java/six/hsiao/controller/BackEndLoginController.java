package six.hsiao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BackEndLoginController {

	
	 @GetMapping("/BackLoginAction")
		public String goToBackLoginAction() {
		 return "back/hsiao/BackLoginAction";
		}
	
}
