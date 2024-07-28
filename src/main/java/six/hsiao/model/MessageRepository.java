package six.hsiao.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  
	 List<Message> findBySenderIdOrReceiverId(Integer senderId, Long receiverId);

	    // 根據接收者 ID 查詢所有訊息
	    List<Message> findByReceiverId(Long receiverId);
}