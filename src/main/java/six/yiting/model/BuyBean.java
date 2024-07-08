package six.yiting.model;



import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;



@Entity 
@Table(name = "storeBuy")
public class BuyBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @Column(name="PURCHASEID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int purchaseId;
	
	
	private boolean checkToInv;

	@DateTimeFormat(pattern = "yyyy/MM/dd") //若要在thymeleaf強制使用本格式，需雙層大括號
	@Temporal(TemporalType.DATE)
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
	public boolean isCheckToInv() {
		return checkToInv;
	}
	public void setCheckToInv(boolean checkToInv) {
		this.checkToInv = checkToInv;
	}
	
	
	

	
}