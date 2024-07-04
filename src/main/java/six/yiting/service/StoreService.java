package six.yiting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.yiting.model.StoresBean;
import six.yiting.model.StoresRepository;

@Service
public class StoreService {
	
	@Autowired
	private StoresRepository storesRepo;

	public List<StoresBean> findAllStores(){
		return storesRepo.findAll();
	}
	
	
	public StoresBean saveStore(StoresBean store) {
		return storesRepo.save(store);
	}
	

}

