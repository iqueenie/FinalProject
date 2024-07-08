package six.yiting.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.hsiao.model.MembersBean;
import six.yiting.model.InventoryBean;
import six.yiting.model.StoreLikeBean;
import six.yiting.model.StoreLikeRepository;
import six.yiting.model.StoresBean;
import six.yiting.model.StoresRepository;

@Service
public class StoreLikeService {
	

	
	@Autowired
	private StoreLikeRepository likeRepo;
	private StoresBean store;

	public List<StoreLikeBean> findAllLikes(){
		return likeRepo.findAll();
	}
	
	
	public StoreLikeBean saveLike(StoreLikeBean like) {
		return likeRepo.save(like);
	}
	
	public void deleteStoreLike(Integer id) {
		likeRepo.deleteById(id);
	}
	
	public StoreLikeBean findLikeById(Integer id) {
		
		Optional<StoreLikeBean> optional = likeRepo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public boolean checkLikeExist(StoresBean store, MembersBean member) {
		
		StoreLikeBean like = likeRepo.findByStoreAndMember(store, member);
		
		if(like == null) {
			return false;
		}
		
		return true;
	}
	
	public long countLikes(StoresBean store) {
		return likeRepo.countByStore(store);
	}

}

