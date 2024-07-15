package six.hsiao.model;

import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface MemberEmailRepository extends CrudRepository<MembersBean,Integer>{
	MembersBean findBymemberEmail(String email);
	
	MembersBean findByResetToken(String resetToken);
	
}
