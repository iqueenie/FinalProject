package six.hsiao.model;

import org.springframework.data.repository.CrudRepository;

public interface MemberEmailRepository extends CrudRepository<MembersBean,Integer>{
	MembersBean findBymemberEmail(String email);
}
