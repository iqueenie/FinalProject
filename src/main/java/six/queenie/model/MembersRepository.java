package six.queenie.model;

import org.springframework.data.jpa.repository.JpaRepository;

import six.hsiao.model.MembersBean;

public interface MembersRepository extends JpaRepository<MembersBean, Integer> {
	MembersBean findByMemberId(Integer memberId);

}
