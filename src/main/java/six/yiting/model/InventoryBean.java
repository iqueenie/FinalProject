package six.yiting.model;

import java.time.LocalDate;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import six.pinhong.model.Product;

@Entity @Table(name="inventory")
public class InventoryBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @Column(name="INVENTORYID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int inventoryId;
	private String buyCode;
	
	@Column(name="PRODUCTID",insertable=false,updatable=false)
	private int productId;
	private int invNum;
	private LocalDate deliveryDate;
	private LocalDate expDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="STOREID")
	private StoresBean store;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PRODUCTID")
	private Product product;
	
	public int getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(int inventoryId) {
		this.inventoryId = inventoryId;
	}
	public String getBuyCode() {
		return buyCode;
	}
	public void setBuyCode(String buyCode) {
		this.buyCode = buyCode;
	}
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getInvNum() {
		return invNum;
	}
	public void setInvNum(int invNum) {
		this.invNum = invNum;
	}
	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public LocalDate getExpDate() {
		return expDate;
	}
	public void setExpDate(LocalDate expDate) {
		this.expDate = expDate;
	}
	public StoresBean getStore() {
		return store;
	}
	public void setStore(StoresBean store) {
		this.store = store;
	}
	

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

	
}