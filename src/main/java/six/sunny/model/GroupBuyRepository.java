package six.sunny.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupBuyRepository extends JpaRepository<GroupBuy, Integer> {

	List<GroupBuy> findByStoreId(Integer storeId);
	
	List<GroupBuy> findByProductId(Integer productId);
	
	List<GroupBuy> findByProductIdAndStoreId(Integer productId, Integer storeId);

}
