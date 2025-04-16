package six.sunny.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import six.hsiao.service.MembersService;
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
	
	@Autowired
	private MembersService membersService;

	
	public List<GroupMember> findAll() {
		return groupMemberRepo.findAll();
	}

	public GroupMember insert(GroupMember groupMember) {
		
		groupMember.setMember(membersService.findByMemberId(groupMember.getMemberId()));
		groupMember.setGroupBuy(groupBuyService.findById(groupMember.getGroupBuyId()));
		groupMember.setPickupStatus("已訂購");
		groupMember.setPaymentStatus("未付款");
		
//		更新GroupMember Total
		GroupBuy groupBuyBean = groupBuyService.findById(groupMember.getGroupBuyId());	
		groupMember.setTotal(groupBuyBean.getPrice() * groupMember.getQuantity());

//		調整GroupMember Status
		String pickupStatus = groupMember.getPickupStatus();
		int quantity = groupMember.getQuantity();
		String groupBuyStatus = groupBuyBean.getGroupBuyStatus();
		groupMember.setPickupStatus(adjustByGb(groupBuyStatus, pickupStatus, quantity));
		
//		更新
		GroupMember insert = groupMemberRepo.save(groupMember);
		
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
	
	public GroupMember update(GroupMember groupMember) {
		
		GroupMember groupMember2 = groupMemberRepo.findById(groupMember.getGroupMemberId()).get();
		
		groupMember2.setMember(membersService.findByMemberId(groupMember.getMemberId()));
		groupMember2.setGroupBuy(groupBuyService.findById(groupMember.getGroupBuyId()));
		groupMember2.setQuantity(groupMember.getQuantity());
		
//		更新GroupMember Total
		GroupBuy groupBuyBean = groupBuyService.findById(groupMember.getGroupBuyId());	
		groupMember2.setTotal(groupBuyBean.getPrice() * groupMember.getQuantity());

//		調整GroupMember Status
		String pickupStatus = groupMember.getPickupStatus();
		int quantity = groupMember.getQuantity();
		String groupBuyStatus = groupBuyBean.getGroupBuyStatus();
		groupMember2.setPickupStatus(adjustByGb(groupBuyStatus, pickupStatus, quantity));
		if (groupMember2.getPickupStatus().equals("已取貨") && groupMember2.getPaymentStatus().equals("未付款")) {
			groupMember2.setPaymentStatus("已到店付款");
		}else {
			groupMember2.setPaymentStatus(groupMember.getPaymentStatus());
		}
		
//		更新
		GroupMember update = groupMemberRepo.save(groupMember2);
		
//		調整GroupBuy數量
		groupBuyService.updateNowQuantityById(update.getGroupBuyId());
		
		return update;
	}
	
	public GroupMember updateQuantity(Integer id, Integer quantity) {
		
		GroupMember groupMember = groupMemberRepo.findById(id).get();

		groupMember.setQuantity(quantity);
		
//		更新GroupMember Total
		GroupBuy groupBuyBean = groupBuyService.findById(groupMember.getGroupBuyId());	
		groupMember.setTotal(groupBuyBean.getPrice() * quantity);
		
//		更新
		GroupMember update = groupMemberRepo.save(groupMember);
		
//		調整GroupBuy數量
		groupBuyService.updateNowQuantityById(update.getGroupBuyId());
		
		return update;
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
	
	public List<GroupMember> findByMemberId(Integer id) {
		return groupMemberRepo.findByMemberId(id);
	}
	
	public GroupMember updateStatusToDelete(Integer id) {
		
		Optional<GroupMember> optional = groupMemberRepo.findById(id);
		if (optional.isPresent()) {
			GroupMember groupMember = optional.get();
			
			groupMember.setPickupStatus("已刪除");
			groupMember.setQuantity(0);
			groupMember.setTotal(0);
			
			groupMemberRepo.save(groupMember);
			
			groupBuyService.updateNowQuantityById(groupMember.getGroupBuyId());
			
			return groupMember;
		}
		
		return null;
		
	}
	
	public GroupMember changeGroupMemberStatus(Integer id, String status) {
		GroupMember gm = groupMemberRepo.findById(id).get();
		gm.setPickupStatus(status);
		if ("已取貨".equals(status) && "未付款".equals(gm.getPaymentStatus())) {
			gm.setPaymentStatus("已到店付款");
		}
		GroupMember save = groupMemberRepo.save(gm);
		
		return save;
		
	}
	
	public Page<GroupMember> findByMemberId(Integer id, Integer page) {
		
		Pageable pgb = PageRequest.of(page-1, 5, Sort.Direction.ASC, "groupBuy.orderDate");
		
		return groupMemberRepo.findByMemberId(id, pgb);
	}
	
	public Page<GroupMember> findByMemberIdAndPickupStatus(Integer id, String pickupStatus, Integer page) {
		
		Pageable pgb = PageRequest.of(page-1, 5, Sort.Direction.ASC, "groupBuy.orderDate");
		
		return groupMemberRepo.findByMemberIdAndPickupStatus(id, pickupStatus, pgb);
	}
	
	public void changePaymentStatus(Integer id) {
		GroupMember groupMember = groupMemberRepo.findById(id).get();
		groupMember.setPaymentStatus("已線上付款");
		groupMemberRepo.save(groupMember);
	}

}
