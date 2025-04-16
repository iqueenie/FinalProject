package six.yiting.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import six.pinhong.model.Product;
import six.yiting.dto.ProductTypeNumberDto;
import six.yiting.model.BuyBean;
import six.yiting.model.InventoryBean;
import six.yiting.model.InventoryRepository;
import six.yiting.model.StoresBean;
import six.yiting.model.StoresRepository;

@Service
public class InventoryService {
	
	@Autowired
	private InventoryRepository invRepo;
	
	@Autowired
	private StoresRepository storeRepo;

	public List<InventoryBean> findAllInventory(){
		return invRepo.findAll();
	}
	
	public List<InventoryBean> findByStore(StoresBean store){
		return invRepo.findByStore(store);
	}
	
	
	public InventoryBean saveInventory(InventoryBean inv) {
		return invRepo.save(inv);
	}
	
	public InventoryBean updateInv(InventoryBean inv) {
		return invRepo.save(inv);
	}
	
	public InventoryBean buyInsertInv(InventoryBean inv) {
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
	
	
	public List<InventoryBean> findByStoreAndProduct(StoresBean store,Integer pid) {
		List<InventoryBean> inv = invRepo.findByStoreAndProductId(store, pid);
		if(inv == null) {
			return null;
		}
		
		return inv;
	}
	
	public Page<InventoryBean> friendlyDetail(Integer pageNumber,StoresBean store,LocalDate expdDate){
		Pageable pgb = PageRequest.of(pageNumber-1, 4, Sort.Direction.ASC, "inventoryId");
		Page<InventoryBean> invResult = invRepo.findByCityAndAreaPage(store, expdDate,pgb);
		return invResult;
		
	}
	
	public Page<InventoryBean> findByType(Integer pageNumber,StoresBean store,LocalDate expdDate,String productType){
		Pageable pgb = PageRequest.of(pageNumber-1, 4, Sort.Direction.ASC, "inventoryId");
		Page<InventoryBean> invResult = invRepo.findByTypePage(store, expdDate, productType, pgb);
		return invResult;
		
	}
	
	public List<String> findProductType(StoresBean store, LocalDate expDate){
		return invRepo.findProductType(store, expDate);
	}
	
	public List<InventoryBean> findByTypeNoPage(StoresBean store,LocalDate expdDate,String productType){
		List<InventoryBean> invResult = invRepo.findByType(store, expdDate, productType);
		return invResult;
		
	}
	
	public boolean inventoryCheck(StoresBean store, LocalDate expDate) {
		List<InventoryBean> byStoreAndExpDate = invRepo.findByStoreAndExpDate(store, expDate);
		if(byStoreAndExpDate==null) {
			return false;
		}else {
			return true;
		}
	}
	
	public InventoryBean findByStoreProductExp(StoresBean store,Product product,LocalDate expDate) {
		InventoryBean inv = invRepo.findByStoreAndProductAndExpDate(store, product, expDate);
		if(inv == null) {
			return null;
		}
		
		return inv;
	}
	
	public void minusOneInventory(Integer storeId,String buyCode) {
		Optional<StoresBean> optional = storeRepo.findById(storeId);
		StoresBean store = optional.get();
		InventoryBean inv = invRepo.findByStoreAndBuyCode(store, buyCode);
		int invNum = inv.getInvNum();
		invNum = invNum-1;
		inv.setInvNum(invNum);
		invRepo.save(inv);
	}
	

}

