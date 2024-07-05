package six.hsiao.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManagementRepository extends JpaRepository<Management, Long> {
	 
	@Query("SELECT m FROM Management m WHERE m.managementAccount = :managementAccount AND m.managementPassword = :managementPassword")
    Management findByAccountAndPassword(@Param("managementAccount") String managementAccount, @Param("managementPassword") String managementPassword);
	
	
	
}
