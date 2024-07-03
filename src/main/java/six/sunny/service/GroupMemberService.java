package six.sunny.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.sunny.model.GroupMember;
import six.sunny.model.GroupMemberRepository;

@Service
public class GroupMemberService{
	
	@Autowired
	private GroupMemberRepository groupMemberRepo;
//	
//	@Autowired
//	@Lazy
//	private IGroupBuyService groupBuyService;
//
	
	public List<GroupMember> findAll() {
		return groupMemberRepo.findAll();
	}

//	@Override
//	public GroupMemberBean insert(GroupMemberBean groupMemberBean) {
//		
////		更新GroupMember Total
//		GroupBuyBean groupBuyBean = groupBuyService.findById(groupMemberBean.getGroupBuyId());	
//		groupMemberBean.setTotal(groupBuyBean.getPrice() * groupMemberBean.getQuantity());
//
////		調整GroupMember Status
//		String pickupStatus = groupMemberBean.getPickupStatus();
//		int quantity = groupMemberBean.getQuantity();
//		String groupBuyStatus = groupBuyBean.getGroupBuyStatus();
//		groupMemberBean.setPickupStatus(adjustByGb(groupBuyStatus, pickupStatus, quantity));
//		
////		更新
//		GroupMemberBean insert = groupMemberDao.insert(groupMemberBean);
//		
////		調整GroupBuy數量
//		groupBuyService.updateNowQuantityById(insert.getGroupBuyId());
//		
//		return insert;
//	}
//
//	@Override
//	public boolean deleteById(Integer id) {
//		
//		Integer gbId = groupMemberDao.findById(id).getGroupBuyId();
//		
//		boolean result = groupMemberDao.deleteById(id);
//		
////		調整GroupBuy數量
//		groupBuyService.updateNowQuantityById(gbId);
//		return result;
//	}
//
//	@Override
//	public GroupMemberBean findById(Integer id) {
//		return groupMemberDao.findById(id);
//	}
//
//	@Override
//	public GroupMemberBean update(GroupMemberBean groupMemberBean) {
////		更新GroupMember Total
//		GroupBuyBean groupBuyBean = groupBuyService.findById(groupMemberBean.getGroupBuyId());
//		groupMemberBean.setTotal(groupBuyBean.getPrice() * groupMemberBean.getQuantity());
//
////		調整GroupMember Status
//		String pickupStatus = groupMemberBean.getPickupStatus();
//		int quantity = groupMemberBean.getQuantity();
//		String groupBuyStatus = groupBuyBean.getGroupBuyStatus();
//		groupMemberBean.setPickupStatus(adjustByGb(groupBuyStatus, pickupStatus, quantity));
//		
////		更新
//		groupMemberDao.update(groupMemberBean);
//		
////		調整GroupBuy數量
//		groupBuyService.updateNowQuantityById(groupBuyBean.getGroupBuyId());
//		
//		return groupMemberBean;
//	}
//

	public List<GroupMember> findByGroupBuyId(Integer id) {
		return groupMemberRepo.findByGroupBuyId(id);
	}
//	
////	處理GroupBuy與GroupMember間的商業邏輯
//	private String adjustByGb(String groupBuyStatus, String pickupStatus, int quantity) {
//		if (groupBuyStatus.equals("未開團") || groupBuyStatus.equals("開團中") || groupBuyStatus.equals("已結單")) {
//			if (pickupStatus.equals("待取貨") || pickupStatus.equals("已取貨") || pickupStatus.equals("未取貨")
//					|| (pickupStatus.equals("已刪除") && quantity != 0)) {
//				return "已訂購";
//			}
//		} else if (groupBuyStatus.equals("不成立")) {
//			return "已刪除";
//		} else if (groupBuyStatus.equals("已到貨")) {
//			if (pickupStatus.equals("已訂購") || pickupStatus.equals("未取貨")
//					|| (pickupStatus.equals("已刪除") && quantity != 0)) {
//				return "待取貨";
//			}
//		} else if (groupBuyStatus.equals("已結束")) {
//			if (pickupStatus.equals("已訂購") || pickupStatus.equals("待取貨")
//					|| (pickupStatus.equals("已刪除") && quantity != 0)) {
//				return "未取貨";
//			}
//		}
//		return pickupStatus;
//	}

}
