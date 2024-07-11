package six.sunny.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupBuyRepository extends JpaRepository<GroupBuy, Integer> {

	List<GroupBuy> findByStoreId(Integer storeId);
	
	List<GroupBuy> findByProductId(Integer productId);
	
	List<GroupBuy> findByProductIdAndStoreId(Integer productId, Integer storeId);
	
	Page<GroupBuy> findByGroupBuyStatus(String groupBuyStatus, Pageable pageable);

}
