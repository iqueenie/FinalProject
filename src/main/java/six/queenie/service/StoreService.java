package six.queenie.service;

import java.util.Optional;

import six.yiting.model.StoresBean;
import six.yiting.model.StoresRepository;

public class StoreService {
	
	private StoresRepository sRepo;
	
	public StoresBean findByStoreId(Integer storeId) {
		
		Optional<StoresBean> optional = sRepo.findById(storeId);
				
				if (optional.isPresent()) {
					StoresBean store = optional.get();
					return store;
				}
				
				return null;
			}
		
	}


