package six.sunny.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.sunny.model.GroupBuyBean;
import six.sunny.model.GroupBuyRepository;
import six.sunny.model.GroupMemberBean;

@Service
@Transactional
public class GroupBuyService{
	
	@Autowired
	private GroupBuyRepository groupBuyRepo;
	

	public List<GroupBuyBean> findAll() {
		return groupBuyRepo.findAll();
	}
	
//	@Override
//	public GroupBuyBean insert(GroupBuyBean groupBuyBean) {
//
////		設定折扣後GroupBuy價格
//		int targetQuantity = groupBuyBean.getTargetQuantity();
//		int price = groupBuyBean.getProduct().getProductPrice();
//		groupBuyBean.setPrice(adjustPrice(price, targetQuantity));
//
////		插入GroupBuy
//		return groupBuyDao.insert(groupBuyBean);
//	}
//
//	@Override
//	public boolean deleteById(Integer id) {
//		return groupBuyDao.deleteById(id);
//	}
//	
//	@Override
//	public GroupBuyBean updateNowQuantityById(Integer id) {
//		
////		取得所有訂購的會員
//		GroupBuyBean gb = groupBuyDao.findById(id);
//		List<GroupMemberBean> gms = gb.getGroupMember();
//		
////		計算訂購總數並更新
//		int newquantity = 0;
//		for(GroupMemberBean gm:gms) {
//			newquantity += gm.getQuantity();
//		}
//		gb.setNowQuantity(newquantity);
//		GroupBuyBean update = groupBuyDao.update(gb);
//		return update;
//	}
//
//	@Override
//	public GroupBuyBean update(GroupBuyBean groupBuyBean) {
//		
////		設定GroupBuy到達時間
//		Date arrivalDate = groupBuyBean.getArrivalDate();
//		if (arrivalDate != null) {
//			LocalDate localDate = arrivalDate.toLocalDate().plusDays(3);
//			groupBuyBean.setEndDate(Date.valueOf(localDate));
//		}
//		
////		設定GroupBuy價格
//		int targetQuantity = groupBuyBean.getTargetQuantity();
//		int price = groupBuyBean.getProduct().getProductPrice();
//		groupBuyBean.setPrice(adjustPrice(price, targetQuantity));
//		
////		更新GroupBuy
//		groupBuyBean = groupBuyDao.update(groupBuyBean);
//		
////		更新GroupMember
//		List<GroupMemberBean> gms = groupMemberService.findGroupMemberByGroupBuyId(groupBuyBean.getGroupBuyId());
//		for (GroupMemberBean gm : gms) {
//			groupMemberService.update(gm);
//		}
//
//		return groupBuyBean;
//	}
//
//
//
//	@Override
//	public List<GroupBuyBean> getByStoreId(int id) {
//		return groupBuyDao.getByStoreId(id);
//	}
//
//	@Override
//	public List<GroupBuyBean> getByProductId(int id) {
//		return groupBuyDao.getByProductId(id);
//	}
//
//	@Override
//	public List<GroupBuyBean> getByProductStoreId(int productId, int storeId) {
//		return groupBuyDao.getByProductStoreId(productId, storeId);
//	}
//
////	調整優惠價格
//	private int adjustPrice(int price, int targetQuantity) {
//		if (targetQuantity >= 30) {
//			price = (int) Math.ceil(price * 0.7);
//		} else if (targetQuantity >= 20) {
//			price = (int) Math.ceil(price * 0.8);
//		} else if (targetQuantity >= 10) {
//			price = (int) Math.ceil(price * 0.9);
//		}
//		return price;
//	}
//
//	@Override
//	public GroupBuyBean findById(Integer id) {
//		return groupBuyDao.findById(id);
//	}


}
