package six.yiting.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.yiting.model.BuyBean;
import six.yiting.model.InventoryBean;
import six.yiting.model.InventoryRepository;
import six.yiting.model.StoresBean;
import six.yiting.model.StoresRepository;

@Service
public class InventoryService {
	
	@Autowired
	private InventoryRepository invRepo;

	public List<InventoryBean> findAllInventory(){
		return invRepo.findAll();
	}
	
	
	public InventoryBean saveInventory(InventoryBean inv) {
		return invRepo.save(inv);
	}
	
	public void deleteInventory(Integer id) {
		invRepo.deleteById(id);
	}
	
	public InventoryBean findInvById(Integer id) {
		
		Optional<InventoryBean> optional = invRepo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public boolean checkInvExist(LocalDate deliveryDate,Integer productId,StoresBean store) {
		
		InventoryBean inv = invRepo.findByProductIdAndDeliveryDateAndStore(productId, deliveryDate, store);
		
		if(inv == null) {
			return false;
		}
		
		return true;
		
	}
	
	public InventoryBean findInvCondition(LocalDate deliveryDate,Integer productId,StoresBean store) {
		
		InventoryBean inv = invRepo.findByProductIdAndDeliveryDateAndStore(productId, deliveryDate, store);
		
		if(inv == null) {
			return null;
		}
		
		return inv;
	} 
	
	
	

}

