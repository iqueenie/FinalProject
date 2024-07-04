package six.hsiao.model;





import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface BackEndLoginRepository extends JpaRepository<BackEndLoginBean,Integer> {
	
	
	@Query("SELECT COUNT(b) > 0 From BackEndLoginBean b WHERE b.managementAccount = :managementAccount AND b.managementPassword = :managementPassword ")
	public boolean findBymanagement(String managementAccount,String managementPassword);
		
	
				
		 
		
	
	
	
	
	
	
}
