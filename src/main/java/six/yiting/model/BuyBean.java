package six.yiting.model;



import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

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



@Entity @Table(name="storeBuy")
public class BuyBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @Column(name="PURCHASEID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int purchaseId;
	


	private LocalDate arrivedDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="STOREID")
	private StoresBean store;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "buy", cascade = CascadeType.ALL)
	private Set<DetailBean> details = new LinkedHashSet<DetailBean>();
	
	
	public BuyBean() {
		
	}
	public int getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(int purchaseId) {
		this.purchaseId = purchaseId;
	}
	
	public LocalDate getArrivedDate() {
		return arrivedDate;
	}
	public void setArrivedDate(LocalDate arrivedDate) {
		this.arrivedDate = arrivedDate;
	}
	public StoresBean getStore() {
		return store;
	}
	public void setStore(StoresBean store) {
		this.store = store;
	}
	
	
	

	
}