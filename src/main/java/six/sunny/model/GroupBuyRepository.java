package six.sunny.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface GroupBuyRepository extends JpaRepository<GroupBuy, Integer> {

	List<GroupBuy> findByStoreId(Integer storeId);
	
	List<GroupBuy> findByProductId(Integer productId);
	
	List<GroupBuy> findByProductIdAndStoreId(Integer productId, Integer storeId);
	
	Page<GroupBuy> findByGroupBuyStatus(String groupBuyStatus, Pageable pageable);

	List<GroupBuy> findByGroupBuyStatusAndStoreId(String groupBuyStatus, Integer storeId);

	Page<GroupBuy> findByGroupBuyStatusAndStoreId(String groupBuyStatus, Integer storeId, Pageable page);
	
    @Query("SELECT gb FROM GroupBuy gb WHERE gb.store.city = :city AND gb.groupBuyStatus = :status")
    Page<GroupBuy> findByCityAndStatus(@Param("city") String city, @Param("status") String status, Pageable pageable);

    @Query("SELECT gb FROM GroupBuy gb WHERE gb.store.city = :city AND gb.store.area = :area AND gb.groupBuyStatus = :status")
    Page<GroupBuy> findByCityAndAreaAndStatus(@Param("city") String city, @Param("area") String area, @Param("status") String status, Pageable pageable);

    @Query("SELECT gb FROM GroupBuy gb WHERE gb.store.city = :city AND gb.store.area = :area AND gb.store.street = :street AND gb.groupBuyStatus = :status")
    Page<GroupBuy> findByCityAreaAndStreetAndStatus(@Param("city") String city, @Param("area") String area, @Param("street") String street, @Param("status") String status, Pageable pageable);
}
