package six.pinhong.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Integer> { 

	// 根据 memberId 和 productId 查找 ProductFavorite
    ProductFavorite findByMemberMemberIdAndProductProductId(Integer memberId, Integer productId);
    
	// memberId 查 所有收藏商品
    List<ProductFavorite> findByMemberMemberId(Integer memberId);
    
    void deleteByProductProductId(Integer productId);
}

