package six.yiting.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.yiting.model.BuyBean;
import six.yiting.model.BuyRepository;
import six.yiting.model.DetailBean;
import six.yiting.model.DetailRepository;
import six.yiting.model.InventoryBean;
import six.yiting.model.InventoryRepository;
import six.yiting.model.StoresBean;
import six.yiting.model.StoresRepository;


@Service
public class BuyService {
	
	@Autowired
	private BuyRepository buyRepo;
	
	@Autowired
	private DetailRepository detailRepo;
	
	@Autowired
	private StoresRepository storesRepo;
	
	@Autowired
	InventoryRepository invRepo;

	public List<BuyBean> findAllBuys(){
		return buyRepo.findAll();
	}
	
	public List<BuyBean> findByStore(StoresBean store){
		return buyRepo.findByStore(store);
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
	
	public List<BuyBean> findByArriveDateAndCheckToInv() {
		LocalDate today = LocalDate.now();
		List<BuyBean> resultList = buyRepo.findByArrivedDateAndCheckToInv(today, false);
		if(resultList.size()>=1) {
			return resultList;
		}else {
			return null;
		}
	}
	
	 @Transactional
	public void buyInsertInv(Integer id) {
		Optional<BuyBean> optional = buyRepo.findById(id);
		BuyBean buy =  optional.get();
		List<DetailBean> details = detailRepo.findByPurchaseId(id);
		List<InventoryBean> invs = new ArrayList<>();
		
		//這張採購單的明細沒有跟存貨有一樣的 false表示沒有一樣
		boolean finalResult=false;
		boolean result = false;
		for(DetailBean detail:details) {
			InventoryBean inv = invRepo.findByProductIdAndDeliveryDateAndStore(detail.getProduct().getProductId(),detail.getBuy().getArrivedDate(),detail.getBuy().getStore());
			if(inv != null) {
				int invNum = inv.getInvNum();
				InventoryBean invbean= new InventoryBean();
				LocalDate deliveryDate = detail.getBuy().getArrivedDate();
				invbean.setDeliveryDate(deliveryDate);
				int storeId = detail.getBuy().getStore().getStoreId();
				Optional<StoresBean> optional2 = storesRepo.findById(storeId);
				StoresBean store =  optional2.get();
				invbean.setStore(store);
				invbean.setProductId(detail.getProduct().getProductId());
				invbean.setProduct(detail.getProduct());
				invbean.setInvNum(detail.getPurchaseNum()+invNum);
				
	 			
	 			int days =detail.getProduct().getProductExpirydate();
	 			LocalDate expDate = deliveryDate.plusDays(days);
	 			invbean.setExpDate(expDate);
	 			String buycode = Integer.toString(detail.getProduct().getProductId()) + expDate.toString();
				buycode = buycode.replace("-", "");
				if (buycode.length() < 12) {
					buycode = String.format("%1$" + 12 + "s", buycode).replace(' ', '0');
				}
				invbean.setBuyCode(buycode);
				buy.setCheckToInv(true);
				buyRepo.save(buy);
	 			
	 			invRepo.save(invbean);
			}else {
				InventoryBean invbean= new InventoryBean();
				LocalDate deliveryDate = detail.getBuy().getArrivedDate();
				invbean.setDeliveryDate(deliveryDate);
				int storeId = detail.getBuy().getStore().getStoreId();
				Optional<StoresBean> optional2 = storesRepo.findById(storeId);
				StoresBean store =  optional2.get();
				invbean.setStore(store);
				invbean.setProductId(detail.getProduct().getProductId());
				invbean.setProduct(detail.getProduct());
				invbean.setInvNum(detail.getPurchaseNum());
				
	 			
	 			int days =detail.getProduct().getProductExpirydate();
	 			LocalDate expDate = deliveryDate.plusDays(days);
	 			invbean.setExpDate(expDate);
	 			String buycode = Integer.toString(detail.getProduct().getProductId()) + expDate.toString();
				buycode = buycode.replace("-", "");
				if (buycode.length() < 12) {
					buycode = String.format("%1$" + 12 + "s", buycode).replace(' ', '0');
				}
				invbean.setBuyCode(buycode);
				buy.setCheckToInv(true);
				buyRepo.save(buy);
	 			
	 			invRepo.save(invbean);
			}
		}
		
	}
	
	
	
}

