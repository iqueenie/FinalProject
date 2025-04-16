package six.pinhong.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    
    @GetMapping("/private/Product/Logs/Filter")
    @ResponseBody
    public List<MemberActionLog> filterLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null || endDate == null) {
            // 如果沒有提供日期，設置預設日期為昨天到今天
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            return memberActionLogService.findByActionAndDateRange(action, yesterday, today);
        }
        
        return memberActionLogService.findByActionAndDateRange(action, startDate, endDate);
    }
}
