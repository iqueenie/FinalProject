package six.sunny.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.sunny.model.GroupBuy;
import six.sunny.model.GroupMember;
import six.sunny.model.GroupMemberRepository;

@Service
public class GroupMemberService{
	
	@Autowired
	private GroupMemberRepository groupMemberRepo;
	
	@Autowired
	@Lazy
	private GroupBuyService groupBuyService;

	
	public List<GroupMember> findAll() {
		return groupMemberRepo.findAll();
	}

	public GroupMember save(GroupMember groupMemberBean) {
		
//		更新GroupMember Total
		GroupBuy groupBuyBean = groupBuyService.findById(groupMemberBean.getGroupBuyId());	
		groupMemberBean.setTotal(groupBuyBean.getPrice() * groupMemberBean.getQuantity());

//		調整GroupMember Status
		String pickupStatus = groupMemberBean.getPickupStatus();
		int quantity = groupMemberBean.getQuantity();
		String groupBuyStatus = groupBuyBean.getGroupBuyStatus();
		groupMemberBean.setPickupStatus(adjustByGb(groupBuyStatus, pickupStatus, quantity));
		
//		更新
		GroupMember insert = groupMemberRepo.save(groupMemberBean);
		
//		調整GroupBuy數量
		groupBuyService.updateNowQuantityById(insert.getGroupBuyId());
		
		return insert;
	}

	public boolean deleteById(Integer id) {
		
		Integer gbId = groupMemberRepo.findById(id).get().getGroupBuyId();
		
        if (groupMemberRepo.existsById(id)) {
        	groupMemberRepo.deleteById(id);
//    		調整GroupBuy數量
    		groupBuyService.updateNowQuantityById(gbId);
            return true;
        } else {
            return false;
        }
	}

	public GroupMember findById(Integer id) {
		Optional<GroupMember> optional = groupMemberRepo.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		} else {
			return null;
		}
	}


	public List<GroupMember> findByGroupBuyId(Integer id) {
		return groupMemberRepo.findByGroupBuyId(id);
	}
	
//	處理GroupBuy與GroupMember間的商業邏輯
	private String adjustByGb(String groupBuyStatus, String pickupStatus, int quantity) {
		if (groupBuyStatus.equals("未開團") || groupBuyStatus.equals("開團中") || groupBuyStatus.equals("已結單")) {
			if (pickupStatus.equals("待取貨") || pickupStatus.equals("已取貨") || pickupStatus.equals("未取貨")
					|| (pickupStatus.equals("已刪除") && quantity != 0)) {
				return "已訂購";
			}
		} else if (groupBuyStatus.equals("不成立")) {
			return "已刪除";
		} else if (groupBuyStatus.equals("已到貨")) {
			if (pickupStatus.equals("已訂購") || pickupStatus.equals("未取貨")
					|| (pickupStatus.equals("已刪除") && quantity != 0)) {
				return "待取貨";
			}
		} else if (groupBuyStatus.equals("已結束")) {
			if (pickupStatus.equals("已訂購") || pickupStatus.equals("待取貨")
					|| (pickupStatus.equals("已刪除") && quantity != 0)) {
				return "未取貨";
			}
		}
		return pickupStatus;
	}

}
