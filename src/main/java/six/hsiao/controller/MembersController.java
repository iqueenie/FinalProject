package six.hsiao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;

@Controller
public class MembersController {
	
	@Autowired
	private MembersService membersservice;
	
	@GetMapping("/GetAllMembers")
	public String getAllProudcts(Model model) {
		List<MembersBean> members = membersservice.findAll();
		model.addAttribute("members", members);
		return "back/hsiao/GetAllMembers";
	}
	
	
	
}
