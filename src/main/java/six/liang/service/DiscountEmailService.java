package six.liang.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.liang.model.AmountDiscount;
import six.liang.model.AmountDiscountRepository;

import java.util.List;

@Service
public class DiscountEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private AmountDiscountRepository amountDiscountRepository;

    @Async
    public void sendPromotionEmailToAllMembers() {
        List<MembersBean> members = membersRepository.findAll();
        for (MembersBean member : members) {
            if (member.getMemberEmail() != null && isValidEmail(member.getMemberEmail())) {
                sendEmail(member.getMemberEmail(), generateEmailContent());
            }
        }
    }

    @Async
    public void sendPromotionEmailForDiscount(Integer discountId) {
        AmountDiscount discount = amountDiscountRepository.findById(discountId).orElse(null);
        if (discount != null) {
            String discountContent = generateEmailContentWithDiscount(discount.getDiscountDescription());
            List<MembersBean> members = membersRepository.findAll();
            for (MembersBean member : members) {
                if (member.getMemberEmail() != null && isValidEmail(member.getMemberEmail())) {
                    sendEmail(member.getMemberEmail(), discountContent);
                }
            }
        }
    }

    private void sendEmail(String to, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("jerryliang900330@gmail.com"); // 設置發件人郵箱
        message.setSubject("最新優惠活動");
        message.setText(content);
        mailSender.send(message);
    }

    private String generateEmailContent() {
        return "尊敬的會員，\n\n" +
                "我們推出了最新的優惠活動！只需分享此優惠訊息，即可獲得會員點數。\n\n" +
                "優惠詳情：\n" +
                "週年慶，滿1000打8折\n" +
                "優惠訊息200字\n\n" +
                "點擊以下鏈接進行分享並獲得點數：\n" +
                "https://yourwebsite.com/share\n\n" +
                "感謝您的支持！";
    }

    private String generateEmailContentWithDiscount(String discountDescription) {
        return "尊敬的會員，\n\n" +
                "EZBUY即將推出最新的優惠活動！\n\n" +
                "優惠詳情：\n" +
                discountDescription + "\n\n" +
                "歡迎您來店或是線上一同參與，感謝您的支持！";
    }

    private boolean isValidEmail(String email) {
        return email.toLowerCase().endsWith("@gmail.com");
    }
}
