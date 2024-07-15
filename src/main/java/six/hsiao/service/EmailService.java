package six.hsiao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import six.hsiao.model.MemberEmailRepository;
import six.hsiao.model.MembersBean;

@Service
public class EmailService {
	
	 @Autowired
	 private JavaMailSender mailSender;
	
	
	@Autowired
	private MemberEmailRepository mRepository;
	
	
	 public void sendResetPasswordEmail(String to, String token) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(to);
	        message.setSubject("重新設定密碼");
	        message.setText("點擊以下網址,更改密碼:\n"+"http://localhost:8080/FinalProject/public/ResetPasswordMain?token=" + token);
	        mailSender.send(message);
	    }
	
}
