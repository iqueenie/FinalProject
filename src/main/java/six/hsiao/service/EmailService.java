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
import six.queenie.model.OrderDetails;
import six.queenie.model.Orders;
import six.queenie.service.CartService;
import six.queenie.service.OrderService;
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
	@Autowired
	private OrderService oService;
	
	
	
	 public void sendResetPasswordEmail(String to, String token) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(to);
	        message.setSubject("重新設定密碼");
	        message.setText("點擊以下網址,更改密碼:\n"+"http://localhost:8080/FinalProject/public/ResetPasswordMain?token=" + token);
	        mailSender.send(message);
	    }
	 
	 public void sendGroupBuyEmail(Integer gbId) {
		GroupBuy groupBuy = groupBuyService.findById(gbId);
		for (GroupMember groupMember : groupBuy.getGroupMember()) {
			if(!groupMember.getPickupStatus().equals("已刪除") && !groupMember.getMember().getMemberEmail().contains("@example.com")) {
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
	 //訂單成立信件
	 public void sendOrderEmail(Integer orderId) {
	        Orders order = oService.getOrderById(orderId);
	        if (order == null || order.getMembers() == null) {
	            throw new RuntimeException("未找到訂單或會員信息");
	        }

	        MembersBean member = order.getMembers();
	        String to = member.getMemberEmail();
	        String subject = "【EZBUY】訂單成立通知: " + order.getOrderId();
	        String text = buildOrderConfirmationEmail(order);

	        try {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);
	            helper.setTo(to);
	            helper.setSubject(subject);
	            helper.setText(text, true);
	            mailSender.send(message);
	        } catch (Exception e) {
	            throw new RuntimeException("發送郵件失敗", e);
	        }
	    }

	    private String buildOrderConfirmationEmail(Orders order) {
	        StringBuilder sb = new StringBuilder();
	        sb.append("<html>");
	        sb.append("<body>");
	        sb.append("<h1>訂單成立通知</h1>");
	        sb.append("<p>尊敬的客戶，您的訂單已成功建立。</p>");
	        sb.append("<p>訂單編號：").append(order.getOrderId()).append("</p>");
	        sb.append("<p>訂單詳情如下：</p>");
	        sb.append("<table>");
	        sb.append("<tr><th>商品名稱</th><th>數量</th><th>價格</th></tr>");
	        for (OrderDetails detail : order.getDetails()) {
	            sb.append("<tr>")
	              .append("<td>").append(detail.getProduct().getProductName()).append("</td>")
	              .append("<td>").append(detail.getQuantity()).append("</td>")
	              .append("<td>").append(detail.getProduct().getProductPrice()).append("</td>")
	              .append("</tr>");
	        }
	        sb.append("</table>");
	        sb.append("<p>總金額：").append(order.getTotal()).append("</p>");
	        sb.append("<p>取貨店鋪：").append(order.getStoresBean().getStoreName()).append("</p>");
	        sb.append("<p>感謝您的購買！</p>");
	        sb.append("</body>");
	        sb.append("</html>");
	        return sb.toString();
	    }
	 
	    }
	 
	

