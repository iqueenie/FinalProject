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
    private int orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private MembersBean members;

    private Date orderDate;
    private int pointUse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amountDiscountId")
    private AmountDiscount amountDiscount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productDiscountId")
    private ProductDiscount productDiscount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storeId")
    private StoresBean storesBean;

    private int   total;
    private int discountMoney;
    private String status;
    private String paymentMethod;
    private Date pickupDate;
    private Date orderSuccessDate;
    private int unpaidCount;
    private int pointGet;
    private int  finalAmount;
   
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orders", cascade = CascadeType.ALL)
    private Set<OrderDetails> details = new LinkedHashSet<>();

    public Orders() {
    }

 
   

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
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

    public int getPointUse() {
        return pointUse;
    }

    public void setPointUse(int pointUse) {
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


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getDiscountMoney() {
        return discountMoney;
    }

    public void setDiscountMoney(int discountMoney) {
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

    public int getUnpaidCount() {
        return unpaidCount;
    }

    public void setUnpaidCount(int unpaidCount) {
        this.unpaidCount = unpaidCount;
    }

    public int getPointGet() {
        return pointGet;
    }

    public void setPointGet(int pointGet) {
        this.pointGet = pointGet;
    }

    public int getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(int finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Set<OrderDetails> getDetails() {
        return details;
    }

    public void setDetails(Set<OrderDetails> details) {
        this.details = details;
    }




	
    
}

