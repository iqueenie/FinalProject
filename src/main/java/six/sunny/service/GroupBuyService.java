package six.sunny.service;

import java.sql.Date;
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
import six.pinhong.model.ProductRepository;
import six.pinhong.service.ProductService;
import six.sunny.model.GroupBuy;
import six.sunny.model.GroupBuyRepository;
import six.sunny.model.GroupMember;
import six.yiting.model.StoresBean;
import six.yiting.service.StoreService;

@Service
public class GroupBuyService{
	
//	TODO
	@Autowired
	private GroupBuyRepository groupBuyRepo;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private GroupMemberService groupMemberService;
	

	public List<GroupBuy> findAll() {
		return groupBuyRepo.findAll();
	}
	
	public GroupBuy insert(GroupBuy groupBuy) {
		
		Product product = productService.findProductById(groupBuy.getProductId());
		StoresBean store = storeService.findStoreById(groupBuy.getStoreId());
		
		groupBuy.setProduct(product);
		groupBuy.setStore(store);

//		設定折扣後GroupBuy價格
		int targetQuantity = groupBuy.getTargetQuantity();
		int price = groupBuy.getProduct().getProductPrice();
		groupBuy.setPrice(adjustPrice(price, targetQuantity));

//		插入GroupBuy
		return groupBuyRepo.save(groupBuy);
	}

	public boolean deleteById(Integer id) {
        if (groupBuyRepo.existsById(id)) {
    		groupBuyRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
	}
	
	public GroupBuy updateNowQuantityById(Integer id) {
		
//		取得所有訂購的會員
		GroupBuy gb = groupBuyRepo.findById(id).get();
		List<GroupMember> gms = gb.getGroupMember();
		
//		計算訂購總數並更新
		int newquantity = 0;
		for(GroupMember gm:gms) {
			newquantity += gm.getQuantity();
		}
		gb.setNowQuantity(newquantity);
		GroupBuy update = groupBuyRepo.save(gb);
		return update;
	}
	
	public GroupBuy changeGroupBuyStatus(Integer id, String status) {
		GroupBuy gb = groupBuyRepo.findById(id).get();
		gb.setGroupBuyStatus(status);
		if (status.equals("已到貨")) {
			LocalDate now = LocalDate.now();
			gb.setArrivalDate(Date.valueOf(now));
			now.plusDays(3);
			gb.setEndDate(Date.valueOf(now.plusDays(3)));
		}
		GroupBuy save = groupBuyRepo.save(gb);
		
		List<GroupMember> gms = groupMemberService.findByGroupBuyId(gb.getGroupBuyId());
		for (GroupMember gm : gms) {
			groupMemberService.update(gm);
		}
		
		return save;
		
	}

	public GroupBuy update(GroupBuy groupBuy) {
		
		GroupBuy groupBuy2 = groupBuyRepo.findById(groupBuy.getGroupBuyId()).get();
		
		groupBuy2.setProduct(productService.findProductById(groupBuy.getProductId()));
		groupBuy2.setStore(storeService.findStoreById(groupBuy.getStoreId()));
		groupBuy2.setOrderDate(groupBuy.getOrderDate());
		groupBuy2.setArrivalDate(groupBuy.getArrivalDate());
		groupBuy2.setTargetQuantity(groupBuy.getTargetQuantity());
		groupBuy2.setGroupBuyStatus(groupBuy.getGroupBuyStatus());
		
//		設定GroupBuy到達時間
		Date arrivalDate = groupBuy.getArrivalDate();
		if (arrivalDate != null) {
			LocalDate localDate = arrivalDate.toLocalDate().plusDays(3);
			groupBuy2.setEndDate(Date.valueOf(localDate));
		}else {
			groupBuy2.setEndDate(null);
		}
		
//		設定GroupBuy價格
		int targetQuantity = groupBuy.getTargetQuantity();
		int price = groupBuy2.getProduct().getProductPrice();
		groupBuy2.setPrice(adjustPrice(price, targetQuantity));
		
//		更新GroupBuy
		GroupBuy update = groupBuyRepo.save(groupBuy2);
		
//		更新GroupMember
		List<GroupMember> gms = groupMemberService.findByGroupBuyId(groupBuy.getGroupBuyId());
		for (GroupMember gm : gms) {
			groupMemberService.update(gm);
		}

		return update;
	}

	public List<GroupBuy> findByStoreId(int id) {
		return groupBuyRepo.findByStoreId(id);
	}

	public List<GroupBuy> findByProductId(int id) {
		return groupBuyRepo.findByProductId(id);
	}

	public List<GroupBuy> findByProductIdAndStoreId(int productId, int storeId) {
		return groupBuyRepo.findByProductIdAndStoreId(productId, storeId);
	}

//	調整優惠價格
	private int adjustPrice(int price, int targetQuantity) {
		if (targetQuantity >= 30) {
			price = (int) Math.ceil(price * 0.7);
		} else if (targetQuantity >= 20) {
			price = (int) Math.ceil(price * 0.8);
		} else if (targetQuantity >= 10) {
			price = (int) Math.ceil(price * 0.9);
		}
		return price;
	}

	public GroupBuy findById(Integer id) {
		Optional<GroupBuy> optional = groupBuyRepo.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}else {
			return null;
		}
	}
	
	public Page<GroupBuy> findByGroupBuyStatus(String status, Integer page) {
		
		Pageable pgb = PageRequest.of(page-1, 5, Sort.Direction.ASC, "orderDate");
		
		return groupBuyRepo.findByGroupBuyStatus(status,pgb);
	}
	
	public List<GroupBuy> findByGroupBuyStatusAndStoreId(String status, Integer storeId) {
		
		return groupBuyRepo.findByGroupBuyStatusAndStoreId(status, storeId);
	}
	
	public Page<GroupBuy> findByGroupBuyStatusAndStoreId(String status, Integer storeId, Integer page) {
		
    	Pageable pgb = PageRequest.of(page-1, 5, Sort.Direction.ASC, "orderDate");
		
		return groupBuyRepo.findByGroupBuyStatusAndStoreId(status, storeId, pgb);
	}
	
    public Page<GroupBuy> findByCityAndStatus(String city, String status, Integer page) {
    	
    	Pageable pgb = PageRequest.of(page-1, 5, Sort.Direction.ASC, "orderDate");
    	
        return groupBuyRepo.findByCityAndStatus(city, status, pgb);
    }

    public Page<GroupBuy> findByCityAndAreaAndStatus(String city, String area, String status, Integer page) {
    	
    	Pageable pgb = PageRequest.of(page-1, 5, Sort.Direction.ASC, "orderDate");
    	
        return groupBuyRepo.findByCityAndAreaAndStatus(city, area, status, pgb);
    }

    public Page<GroupBuy> findByCityAreaAndStreetAndStatus(String city, String area, String street, String status, Integer page) {
    	
    	Pageable pgb = PageRequest.of(page-1, 5, Sort.Direction.ASC, "orderDate");
    	
        return groupBuyRepo.findByCityAreaAndStreetAndStatus(city, area, street, status, pgb);
    }

}
