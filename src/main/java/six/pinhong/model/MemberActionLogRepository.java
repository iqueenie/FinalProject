package six.pinhong.model;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberActionLogRepository extends JpaRepository<MemberActionLog, Integer> {
	
	 List<MemberActionLog> findByAction(String action);
	    List<MemberActionLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
	    List<MemberActionLog> findByActionAndTimestampBetween(String action, LocalDateTime start, LocalDateTime end);
	
}
