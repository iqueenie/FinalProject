package six.liang.model;

import java.sql.Date;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name="amountDiscount")
@Component
public class AmountDiscount {

@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private int discountId; 
	 private String discountName;
	 private int minPurchaseAmount;
	 private int discountPercentage;
	 private Date startDate;
	 private Date endDate;
	 private int isActive;
	 
	public int getDiscountId() {
		return discountId;
	}
	public void setDiscountId(int discountId) {
		this.discountId = discountId;
	}
	public String getDiscountName() {
		return discountName;
	}
	public void setDiscountName(String discountName) {
		this.discountName = discountName;
	}
	public int getMinPurchaseAmount() {
		return minPurchaseAmount;
	}
	public void setMinPurchaseAmount(int minPurchaseAmount) {
		this.minPurchaseAmount = minPurchaseAmount;
	}
	public int getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(int discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	
	
}
