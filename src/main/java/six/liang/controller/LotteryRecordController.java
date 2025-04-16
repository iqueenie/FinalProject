package six.liang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;
import six.liang.model.Award;
import six.liang.model.LotteryRecord;
import six.liang.service.AwardService;
import six.liang.service.LotteryRecordService;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LotteryRecordController {

    @Autowired
    private LotteryRecordService lotteryRecordService;
    
    @Autowired
	private MembersService membersService;
    
    @Autowired
	private AwardService awardService;

    @GetMapping("/public/front/lottery")
    public String showLotteryPage(HttpSession session, Model model) {
        MembersBean loggedInMember = (MembersBean) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return "redirect:/public/frontLoginMain"; // 如果未登錄，重定向到登錄頁面
        }

        boolean hasDrawnToday = lotteryRecordService.hasDrawnToday(loggedInMember.getMemberAccount());
        int remainingDraws = hasDrawnToday ? 0 : 1;
        model.addAttribute("remainingDraws", remainingDraws);

        String memberAccount = loggedInMember.getMemberAccount();
        List<LotteryRecord> lotteryRecords = lotteryRecordService.getRecentRecords(memberAccount, 3);
        model.addAttribute("lotteryRecords", lotteryRecords);

        return "front/liang/lottery";
    }

    @PostMapping("/public/front/lottery/draw")
    @ResponseBody
    public ResponseEntity<?> drawLottery(HttpSession session) {
        MembersBean loggedInMember = (MembersBean) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(401).body(Map.of("message", "未登錄")); // 如果未登錄，返回401狀態碼
        }

        System.out.println("收到抽獎請求"); // 調試訊息
        try {
            String memberAccount = loggedInMember.getMemberAccount();
            System.out.println("會員帳號: " + memberAccount); // 調試訊息
            Award award = lotteryRecordService.drawLottery(memberAccount);
            System.out.println("抽獎成功，獲得獎項: " + award.getName() + ", 點數: " + award.getPoints()); // 調試訊息
            
            MembersBean updatedMember = membersService.findByMemberAccount(memberAccount);
            session.setAttribute("loggedInMember", updatedMember);
            
            return ResponseEntity.ok(Map.of("awardName", award.getName(), "points", award.getPoints()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage())); // 返回403狀態碼，表示一天只能抽一次獎
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
 // 查詢所有抽獎記錄
    @GetMapping("/private/lotteryRecords")
    public String showLotteryRecordsPage(Model model) {
        model.addAttribute("members", membersService.findAll());
        model.addAttribute("awards", awardService.findAllAwards());
        model.addAttribute("records", lotteryRecordService.getAllRecords()); 
        return "back/liang/lotteryRecords"; 
    }

    @GetMapping("/private/lotteryRecords/filter")
    @ResponseBody
    public List<LotteryRecord> filterLotteryRecords(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<LotteryRecord> records = lotteryRecordService.filterRecords(startDate, endDate);
        records.forEach(record -> {
            System.out.println("Record ID: " + record.getId());
            System.out.println("Member Name: " + record.getMemberBean().getMemberName());
            System.out.println("Award Name: " + record.getAward().getName());
        });
        return records;
    }

    @GetMapping("/private/lotteryRecords/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"lottery_records.csv\"");
        PrintWriter writer = response.getWriter();
        writer.println("ID,Member ID,Draw Date,Award ID");

        List<LotteryRecord> records = lotteryRecordService.getAllRecords();
        for (LotteryRecord record : records) {
            writer.println(record.getId() + "," + record.getMemberId() + "," + record.getDrawDate() + "," + record.getAward().getId());
        }
    }
    
    @GetMapping("/private/statistics")
    public String showStatisticsPage() {
        return "back/liang/statistics"; 
    }

    @GetMapping("/private/statistics/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatisticsData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<LotteryRecord> lotteryRecords = lotteryRecordService.filterRecords(startDate, endDate);

        Map<String, Integer> dailyDraws = new HashMap<>();
        Map<String, Integer> awardCounts = new HashMap<>();
        Map<String, Integer> memberCounts = new HashMap<>();
        Map<String, Integer> ageGroupCounts = new HashMap<>();

        for (LotteryRecord record : lotteryRecords) {
            String date = record.getDrawDate().toString();
            String awardName = record.getAward().getName();
            String memberName = record.getMemberBean().getMemberName();

            dailyDraws.put(date, dailyDraws.getOrDefault(date, 0) + 1);
            awardCounts.put(awardName, awardCounts.getOrDefault(awardName, 0) + 1);
            memberCounts.put(memberName, memberCounts.getOrDefault(memberName, 0) + 1);

            
            LocalDate birthDate = record.getMemberBean().getMemberBirthDate();
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            String ageGroup = getAgeGroup(age);
            ageGroupCounts.put(ageGroup, ageGroupCounts.getOrDefault(ageGroup, 0) + 1);
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("dailyDraws", dailyDraws);
        responseData.put("awardCounts", awardCounts);
        responseData.put("memberCounts", memberCounts);
        responseData.put("ageGroupCounts", ageGroupCounts);

        return ResponseEntity.ok(responseData);
    }

    private String getAgeGroup(int age) {
        if (age < 18) {
            return "0-17";
        } else if (age <= 24) {
            return "18-24";
        } else if (age <= 34) {
            return "25-34";
        } else if (age <= 44) {
            return "35-44";
        } else if (age <= 54) {
            return "45-54";
        } else if (age <= 64) {
            return "55-64";
        } else {
            return "65+";
        }
    }

}
