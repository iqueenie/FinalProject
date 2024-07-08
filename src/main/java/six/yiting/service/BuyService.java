package six.yiting.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import six.yiting.model.BuyBean;
import six.yiting.model.BuyRepository;
import six.yiting.model.StoresBean;


@Service
public class BuyService {
	
	@Autowired
	private BuyRepository buyRepo;

	public List<BuyBean> findAllBuys(){
		return buyRepo.findAll();
	}
	
	
	public BuyBean saveBuy(BuyBean store) {
		return buyRepo.save(store);
	}
	
	public void deleteBuy(Integer id) {
		buyRepo.deleteById(id);
	}
	
	public BuyBean findBuyById(Integer id) {
		
		Optional<BuyBean> optional = buyRepo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public boolean checkBuyExist(LocalDate arrivedDate,StoresBean store) {
		
		BuyBean buy = buyRepo.findByArrivedDateAndStore(arrivedDate, store);
		
		if(buy == null) {
			return false;
		}
		
		return true;
		
	}
}

