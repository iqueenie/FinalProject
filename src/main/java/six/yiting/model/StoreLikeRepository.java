package six.yiting.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import six.hsiao.model.MembersBean;
import java.util.List;




public interface StoreLikeRepository extends JpaRepository<StoreLikeBean, Integer>  {
	
	StoreLikeBean findByStoreAndMember(StoresBean store, MembersBean member);
	
	long countByStore(StoresBean store);
	
	@Transactional
	@Modifying
	@Query(value="Delete from StoreLikeBean WHERE store= :store AND member= :member")
	void deleteLikByStoreAndMember(@Param(value = "store") StoresBean store, @Param(value = "member") MembersBean member);
	
	List<StoreLikeBean> findByMember(MembersBean member);

}
