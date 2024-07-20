package six.pinhong.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Integer> { 

	// memberId 查 所有收藏商品
    @Query("SELECT pf FROM ProductFavorite pf WHERE pf.members.memberId = :memberId")
    List<ProductFavorite> findByMemberMemberId(Integer memberId);

}

