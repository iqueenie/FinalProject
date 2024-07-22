package six.sunny.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import six.hsiao.model.MembersBean;

@Entity @Table(name = "groupmember")
@Component
public class GroupMember {
	
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer groupMemberId;

@Column(name = "groupBuyId", insertable = false, updatable = false)
private Integer groupBuyId;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "groupBuyId")
private GroupBuy groupBuy;

@Column(name = "memberId", insertable = false, updatable = false)
private Integer memberId;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "memberId")
private MembersBean member;
private Integer quantity;
private Integer total;
private String pickupStatus = "已訂購";
private String paymentStatus = "未付款";

public Integer getGroupMemberId() {
	return groupMemberId;
}
public void setGroupMemberId(int groupMemberId) {
	this.groupMemberId = groupMemberId;
}
public Integer getQuantity() {
	return quantity;
}
public void setQuantity(int quantity) {
	this.quantity = quantity;
}
public Integer getTotal() {
	return total;
}
public void setTotal(int total) {
	this.total = total;
}
public String getPickupStatus() {
	return pickupStatus;
}
public void setPickupStatus(String pickupStatus) {
	this.pickupStatus = pickupStatus;
}
public GroupBuy getGroupBuy() {
	return groupBuy;
}
public void setGroupBuy(GroupBuy groupBuy) {
	this.groupBuy = groupBuy;
}
public MembersBean getMember() {
	return member;
}
public void setMember(MembersBean member) {
	this.member = member;
}
public Integer getMemberId() {
	return memberId;
}
public String getMemberName() {
	return member.getMemberName();
}
public Integer getGroupBuyId() {
	return groupBuyId;
}
public void setGroupBuyId(int groupBuyId) {
	this.groupBuyId = groupBuyId;
}
public void setMemberId(int memberId) {
	this.memberId = memberId;
}
public String getPaymentStatus() {
	return paymentStatus;
}
public void setPaymentStatus(String paymentStatus) {
	this.paymentStatus = paymentStatus;
}
}
