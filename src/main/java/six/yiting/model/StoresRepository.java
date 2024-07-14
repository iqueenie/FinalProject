package six.yiting.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface StoresRepository extends JpaRepository<StoresBean, Integer>  {

	List<StoresBean> findByCity(String city);
	
	@Query(value="select area from StoresBean group by city,area having  city= :city")
	List<String> findAreaByCity(@Param(value = "city") String city);
	
	@Query(value="select street from StoresBean group by city,area,street having city=:city and area=:area")
	List<String> findStreetByArea(@Param(value = "city") String city,@Param(value = "area") String area);
	
	long countByCityAndArea(String city, String area);
	
	long countByCityAndAreaAndStreet(String city, String area, String street);
	
	List<StoresBean> findByCityAndArea(String city, String area);
	
	List<StoresBean> findByCityAndAreaAndStreet(String city, String area, String street);
	
}
