package six.queenie.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.yiting.model.StoresBean;
import six.yiting.model.StoresRepository;
@Service
public class StoresService {
	@Autowired
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


