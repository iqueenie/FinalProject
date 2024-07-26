package six.queenie.model;

import java.sql.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import six.hsiao.model.*;
import six.liang.model.AmountDiscount;
import six.liang.model.ProductDiscount;
import six.yiting.model.StoresBean;


@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private MembersBean members;

    private Date orderDate;
    private Integer pointUse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amountDiscountId")
    private AmountDiscount amountDiscount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productDiscountId")
    private ProductDiscount productDiscount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId")
    private StoresBean storesBean;

    private Integer   total;
    private Integer discountMoney;
    private String status;
    private String paymentMethod;
    private Date pickupDate;
    private Date orderSuccessDate;
    private Integer unpaidCount;
    private Integer pointGet;
    private Integer  finalAmount;
    private String logisticsId;
    private String logisticStatus;
   
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orders", cascade = CascadeType.ALL)
    private Set<OrderDetails> details = new LinkedHashSet<>();
    public static final String STATUS_PENDING_SHIPMENT = "待出貨";
    public static final String STATUS_PENDING_RECEIPT = "待收貨";
    public static final String STATUS_COMPLETED = "訂單已完成";
    public static final String STATUS_CANCELLED = "不成立";
    public Orders() {
    }

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public MembersBean getMembers() {
		return members;
	}

	public void setMembers(MembersBean members) {
		this.members = members;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Integer getPointUse() {
		return pointUse;
	}

	public void setPointUse(Integer pointUse) {
		this.pointUse = pointUse;
	}

	public AmountDiscount getAmountDiscount() {
		return amountDiscount;
	}

	public void setAmountDiscount(AmountDiscount amountDiscount) {
		this.amountDiscount = amountDiscount;
	}

	public ProductDiscount getProductDiscount() {
		return productDiscount;
	}

	public void setProductDiscount(ProductDiscount productDiscount) {
		this.productDiscount = productDiscount;
	}

	public StoresBean getStoresBean() {
		return storesBean;
	}

	public void setStoresBean(StoresBean storesBean) {
		this.storesBean = storesBean;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getDiscountMoney() {
		return discountMoney;
	}

	public void setDiscountMoney(Integer discountMoney) {
		this.discountMoney = discountMoney;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Date getPickupDate() {
		return pickupDate;
	}

	public void setPickupDate(Date pickupDate) {
		this.pickupDate = pickupDate;
	}

	public Date getOrderSuccessDate() {
		return orderSuccessDate;
	}

	public void setOrderSuccessDate(Date orderSuccessDate) {
		this.orderSuccessDate = orderSuccessDate;
	}

	public Integer getUnpaidCount() {
		return unpaidCount;
	}

	public void setUnpaidCount(Integer unpaidCount) {
		this.unpaidCount = unpaidCount;
	}

	public Integer getPointGet() {
		return pointGet;
	}

	public void setPointGet(Integer pointGet) {
		this.pointGet = pointGet;
	}

	public Integer getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(Integer finalAmount) {
		this.finalAmount = finalAmount;
	}
	
	
	public Set<OrderDetails> getDetails() {
		return details;
	}


	public void setDetails(Set<OrderDetails> details) {
		this.details = details;
	}
	public String getLogisticsId() {
		return logisticsId;
	}
	
	public void setLogisticsId(String logisticsId) {
		this.logisticsId = logisticsId;
	}

	public String getLogisticStatus() {
		return logisticStatus;
	}

	public void setLogisticStatus(String logisticStatus) {
		this.logisticStatus = logisticStatus;
	}

}
 
   

    