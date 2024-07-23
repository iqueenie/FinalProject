package six.yiting.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	long countByCityAndStreet(String city,String street);
	
	List<StoresBean> findByCityAndArea(String city, String area);
	
	@Query(value="from StoresBean WHERE city= :city AND area= :area")
	Page<StoresBean> findByCityAndAreaPage(@Param(value = "city") String city, @Param(value = "area") String area,Pageable pgb);
	
	List<StoresBean> findByCityAndStreet(String city, String street);
	
	List<StoresBean> findByCityAndAreaAndStreet(String city, String area, String street);
	
	@Query(value="SELECT DISTINCT city, area FROM StoresBean WHERE cityNum= :cityNum ")
	String findByZip(@Param(value = "cityNum") String cityNum);
	
	@Query("from StoresBean where storeName like %:storeName%")
	List<StoresBean> findStoreByName(@Param(value = "storeName") String storeName);
	
	@Query(value="SELECT COUNT(*)from StoresBean where storeName like %:storeName%")
	long countByWordStoreName(@Param(value = "storeName") String storeName);

    @Query("SELECT DISTINCT s.city FROM StoresBean s")
    List<String> findDistinctCities();
    
    
	
}
