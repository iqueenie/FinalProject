package six.yiting.model;


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

@Entity @Table(name="buyDetail")
public class DetailBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @Column(name="BUYRECID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int buyRecId;
	
	@Column(name="PURCHASEID",insertable=false,updatable=false)
	private int purchaseId;
	
	private int purchaseNum;
	
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PURCHASEID")
	private BuyBean buy;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PRODID")
	private Product product;
	
	public int getBuyRecId() {
		return buyRecId;
	}
	public void setBuyRecId(int buyRecId) {
		this.buyRecId = buyRecId;
	}
	
	public int getPurchaseNum() {
		return purchaseNum;
	}
	public void setPurchaseNum(int purchaseNum) {
		this.purchaseNum = purchaseNum;
	}
	public BuyBean getBuy() {
		return buy;
	}
	public void setBuy(BuyBean buy) {
		this.buy = buy;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public int getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(int purchaseId) {
		this.purchaseId = purchaseId;
	}
	
}