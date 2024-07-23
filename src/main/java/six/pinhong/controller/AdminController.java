package six.pinhong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import six.pinhong.model.MemberActionLog;
import six.pinhong.service.MemberActionLogService;

@Controller
public class AdminController {
	
    @Autowired
    private MemberActionLogService memberActionLogService;

    @GetMapping("/private/Product/Logs")
    public String getLogs(Model model) {
        List<MemberActionLog> logs = memberActionLogService.getAllLogs();
        model.addAttribute("logs", logs);
        return "back/pinhong/logs";
    }
}
