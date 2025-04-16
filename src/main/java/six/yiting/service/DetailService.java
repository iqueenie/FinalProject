package six.yiting.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.yiting.model.DetailBean;
import six.yiting.model.DetailRepository;


@Service
public class DetailService {
	
	
	@Autowired
	private DetailRepository detailRepo;

	public List<DetailBean> findAllDetails(){
		return detailRepo.findAll();
	}
	
	
	public DetailBean saveDetail(DetailBean store) {
		return detailRepo.save(store);
	}
	
	public void deleteDetail(Integer id) {
		detailRepo.deleteById(id);
	}
	
	public void deleteDetailByPurchaseId(Integer id) {
		detailRepo.deleteByPurchaseId(id);;
	}
	
	public DetailBean findDetailById(Integer id) {
		
		Optional<DetailBean> optional = detailRepo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public List<DetailBean> findByPurchaseId(Integer id){
		return detailRepo.findByPurchaseId(id);
	}
	

}

