package six.yiting.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface DetailRepository extends JpaRepository<DetailBean, Integer>  {

	List<DetailBean> findByPurchaseId(Integer id);
	
	void deleteByPurchaseId(Integer id);
}
