package six.sunny.model;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import six.pinhong.model.Product;
import six.yiting.model.StoresBean;

@Entity
@Table(name = "groupbuy")
@Component
public class GroupBuy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer groupBuyId;
	
	@Column(name = "productId", insertable = false, updatable = false)
	private Integer productId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "productId")
	private Product product;
	
	@Column(name = "storeId", insertable = false, updatable = false)
	private Integer storeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "storeId")
	private StoresBean store;
	private Integer price;
	private Integer targetQuantity;
	private Integer nowQuantity = 0;
	private Date orderDate;
	private Date arrivalDate = null;
	private Date endDate = null;
	private String groupBuyStatus = "未開團";

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "groupBuy", cascade = CascadeType.ALL)
	private List<GroupMember> groupMember;

	public Integer getGroupBuyId() {
		return groupBuyId;
	}

	public void setGroupBuyId(int groupBuyId) {
		this.groupBuyId = groupBuyId;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Integer getTargetQuantity() {
		return targetQuantity;
	}

	public void setTargetQuantity(int targetQuantity) {
		this.targetQuantity = targetQuantity;
	}

	public Integer getNowQuantity() {
		return nowQuantity;
	}

	public void setNowQuantity(int nowQuantity) {
		this.nowQuantity = nowQuantity;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getGroupBuyStatus() {
		return groupBuyStatus;
	}

	public void setGroupBuyStatus(String groupBuyStatus) {
		this.groupBuyStatus = groupBuyStatus;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public StoresBean getStore() {
		return store;
	}

	public void setStore(StoresBean store) {
		this.store = store;
	}

	public List<GroupMember> getGroupMember() {
		return groupMember;
	}

	public void setGroupMember(List<GroupMember> groupMember) {
		this.groupMember = groupMember;
	}

	public Integer getProductId() {
		return productId;
	}
	
	public String getProductName() {
		return product.getProductName();
	}

	public Integer getStoreId() {
		return storeId;
	}

	public String getStoreName() {
		return store.getStoreName();
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	
	public Integer getProductPrice() {
		return product.getProductPrice();
	}
	
	
}
