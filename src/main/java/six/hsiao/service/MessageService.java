package six.hsiao.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.hsiao.model.Message;
import six.hsiao.model.MessageRepository;



@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private MembersRepository  membersRepository;

    private static final Long ADMIN_ID = 1L; // 管理員的 ID
    
    
    // 會員向管理員發送訊息
    public void sendMessageFromMember(Integer senderId, String content) {
        Message message = new Message();
        message.setSenderId(senderId); // 使用 Integer
        message.setReceiverId(ADMIN_ID); // 使用 Long
        message.setMessageContent(content);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }

    // 管理員向單一會員發送訊息
    public void sendMessageFromAdmin(Long receiverMemberId, String content) {
        Message message = new Message();
        message.setSenderId(null); // 管理員 設為 null    用常數設定
        message.setReceiverId(receiverMemberId); // 使用 Long
        message.setMessageContent(content);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }
    
    //發送訊息給所有會員
    public void sendMessageFromAdminToAllMembers(String content) {
    	
    	List<MembersBean> allMembers = membersRepository.findAll();
    	
    	
    	for (MembersBean member : allMembers) {
    		sendMessageFromAdmin(member.getMemberId().longValue(), content);
    	}
    }

    // 查詢管理員的訊息
    public List<Message> getMessagesForAdmin() {
        return messageRepository.findByReceiverId(ADMIN_ID);
    }

    // 查詢會員的訊息
    public List<Message> getMessagesForMember(Integer memberId) {
        return messageRepository.findBySenderIdOrReceiverId(memberId, ADMIN_ID);
    }
    
    // 查詢所有會員發送給管理員的訊息
    public List<Message> getAllMessagesFromMembers() {
        return messageRepository.findByReceiverId(ADMIN_ID);
    }
    
   
    
}
    
