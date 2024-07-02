package six.hsiao.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BackEndLoginRepository extends JpaRepository<BackEndLoginBean,Integer> {
	
	
	
	
	 @Query("SELECT management FROM BackEndLoginBean  WHERE management.managementAccount = :account AND management.managementPassword = :password")
	    BackEndLoginBean findByAccountAndPassword(@Param("account") String managementAccount, @Param("password") String managementPassword);
	
	
}
