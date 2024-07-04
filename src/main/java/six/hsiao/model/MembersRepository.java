package six.hsiao.model;

import org.springframework.data.jpa.repository.JpaRepository;



public interface MembersRepository  extends JpaRepository<MembersBean, Integer>{
	MembersBean findByMemberId(Integer memberId);
}
