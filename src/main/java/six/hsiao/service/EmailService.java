package six.hsiao.service;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import six.hsiao.model.MemberEmailRepository;
import six.hsiao.model.MembersBean;
import six.sunny.model.GroupBuy;
import six.sunny.model.GroupMember;
import six.sunny.service.GroupBuyService;

@Service
public class EmailService {
	
	 @Autowired
	 private JavaMailSender mailSender;
	
	
	@Autowired
	private MemberEmailRepository mRepository;
	
	@Autowired
	private GroupBuyService groupBuyService;
	
	
	 public void sendResetPasswordEmail(String to, String token) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(to);
	        message.setSubject("重新設定密碼");
	        message.setText("點擊以下網址,更改密碼:http://localhost:8080/FinalProject/front/ForgetThePassword" + token);
	        mailSender.send(message);
	    }
	 
	 public void sendGroupBuyEmail(Integer gbId) {
		GroupBuy groupBuy = groupBuyService.findById(gbId);
		for (GroupMember groupMember : groupBuy.getGroupMember()) {
			String to = groupMember.getMember().getMemberEmail();
			String subject = "【EZBUY】團購商品到貨通知";
			String htmlBody = "<h2>親愛的 "+ groupMember.getMember().getMemberName() + " ，您好！</h2>\r\n"
					+ "\r\n"
					+ "    <p>感謝您向EZBUY購買商品，您所訂購的團購商品已經到貨。</p>\r\n"
					+ "\r\n"
					+ "    <h3>訂單詳情：</h3>\r\n"
					+ "    <ul>\r\n"
					+ "        <li>商品名稱： " + groupBuy.getProductName() + "</li>\r\n"
					+ "        <li>訂購數量： " + groupMember.getQuantity() +"</li>\r\n"
					+ "        <li>總金額： $" + groupMember.getTotal() + "</li>\r\n"
					+ "    </ul>\r\n"
					+ "\r\n"
					+ "    <h3>取貨方式：門市自取</h3>\r\n"
					+ "    <ul>\r\n"
					+ "        <li>取貨門市： " + groupBuy.getStoreName() + "</li>\r\n"
					+ "        <li>取貨地點： " + groupBuy.getStore().getCity()+groupBuy.getStore().getArea()+groupBuy.getStore().getStreet()+groupBuy.getStore().getDetail() + "</li>\r\n"
					+ "        <li>取貨日期： " + groupBuy.getArrivalDate() + " 至 " + groupBuy.getEndDate() + "</li>\r\n"
					+ "    </ul>\r\n"
					+ "\r\n"
					+ "    <p>再次感謝您的支持與信任，祝您有美好的一天！</p>";
			
			try {
				MimeMessage message = mailSender.createMimeMessage();
		        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		        helper.setTo(to);
		        helper.setSubject(subject);
		        helper.setText(htmlBody, true);
		        mailSender.send(message);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}

	}
	
}
