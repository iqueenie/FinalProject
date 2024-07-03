package six.sunny.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
	List<GroupMember> findByGroupBuyId(Integer groupBuyId);
}
