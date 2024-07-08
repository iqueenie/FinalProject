package six.hsiao.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MembersRepository extends JpaRepository<MembersBean, Integer> {
	MembersBean findByMemberId(Integer memberId);

	@Query("SELECT YEAR(u.registrationDate) as year, MONTH(u.registrationDate) as month, COUNT(u) as count "
			+ "FROM MembersBean u " 
			+ "GROUP BY YEAR(u.registrationDate), MONTH(u.registrationDate) "
			+ "ORDER BY YEAR(u.registrationDate), MONTH(u.registrationDate)")
	List<Object[]> countRegistrationsPerMonth();
}
