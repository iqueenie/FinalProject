package six.yiting.model;

import org.springframework.data.jpa.repository.JpaRepository;
import six.hsiao.model.MembersBean;
import six.yiting.model.StoresBean;



public interface StoreLikeRepository extends JpaRepository<StoreLikeBean, Integer>  {
	
	StoreLikeBean findByStoreAndMember(StoresBean store, MembersBean member);
	
	long countByStore(StoresBean store);

}
