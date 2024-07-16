package six.hsiao.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembersRepository extends JpaRepository<MembersBean, Integer> {


	@Query("SELECT YEAR(u.registrationDate) as year, MONTH(u.registrationDate) as month, COUNT(u) as count "
			+ "FROM MembersBean u " 
			+ "WHERE u.registrationDate >= :startDate "
			+ "GROUP BY YEAR(u.registrationDate), MONTH(u.registrationDate) "
			+ "ORDER BY YEAR(u.registrationDate), MONTH(u.registrationDate)")
	List<Object[]> countRegistrationsPerMonth(@Param("startDate") LocalDate startDate);

	MembersBean findByMemberAccount(String memberAccount);
	
	MembersBean  findByMemberAccountAndMemberPassword(String memberAccount, String memberPassword);
	
	
}
