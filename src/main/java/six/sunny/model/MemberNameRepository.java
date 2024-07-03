package six.sunny.model;

import org.springframework.data.jpa.repository.JpaRepository;

import six.hsiao.model.MembersBean;

public interface MemberNameRepository extends JpaRepository<MembersBean, Integer> {

}
