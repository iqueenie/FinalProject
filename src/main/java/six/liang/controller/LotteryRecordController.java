package six.liang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import six.hsiao.model.MembersBean;
import six.liang.model.Award;
import six.liang.model.LotteryRecord;
import six.liang.service.LotteryRecordService;

import java.util.List;
import java.util.Map;

@Controller
public class LotteryRecordController {

    @Autowired
    private LotteryRecordService lotteryRecordService;

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
            return ResponseEntity.ok(Map.of("awardName", award.getName(), "points", award.getPoints()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage())); // 返回403狀態碼，表示一天只能抽一次獎
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

}
