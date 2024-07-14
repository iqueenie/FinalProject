package six.yiting.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


/**
 * 
 */
@Entity @Table(name="stores")
public class StoresBean{
	
	@Id @Column(name="STOREID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int storeId;
	private String storeName;
	private String cityNum;
	private String city;
	private String area;
	private String street;
	private String detail;
	private String tel;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "store", cascade = CascadeType.ALL)
	private Set<BuyBean> storeBuys = new LinkedHashSet<BuyBean>();
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "store", cascade = CascadeType.ALL)
	private Set<InventoryBean> storeInvs = new LinkedHashSet<InventoryBean>();
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "store", cascade = CascadeType.ALL)
	private Set<StoreLikeBean> storeLikes = new LinkedHashSet<StoreLikeBean>();
	
	
	public StoresBean() {
		
	}
	
	public StoresBean(String storeName, String cityNum, String city, String area, String street,String detail, String tel) {
		this.storeName = storeName;
		this.cityNum = cityNum;
		this.city = city;
		this.area = area;
		this.street = street;
		this.detail=detail;
		this.tel = tel;
	}
	
	public Set<BuyBean> getStoreBuys() {
		return storeBuys;
	}
	public void setStoreBuys(Set<BuyBean> storeBuys) {
		this.storeBuys = storeBuys;
	}
	public int getStoreId() {
		return storeId;
	}
	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getCityNum() {
		return cityNum;
	}
	public void setCityNum(String cityNum) {
		this.cityNum = cityNum;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Set<InventoryBean> getStoreInvs() {
		return storeInvs;
	}
	public void setStoreInvs(Set<InventoryBean> storeInvs) {
		this.storeInvs = storeInvs;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	
}