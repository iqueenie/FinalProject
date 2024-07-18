package six.sunny.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
	List<GroupMember> findByGroupBuyId(Integer groupBuyId);
	List<GroupMember> findByMemberId(Integer memberId);
	Page<GroupMember> findByMemberId(Integer memberId, Pageable pageable);
	Page<GroupMember> findByMemberIdAndPickupStatus(Integer memberId, String pickupStatus, Pageable pageable);
}
