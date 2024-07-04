package six.hsiao.model;

import java.time.LocalDate;


import org.springframework.format.annotation.DateTimeFormat;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="members")
public class MembersBean {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberId;
	
	@Column(nullable = true)//這裡有改
	private byte[] memberPhoto;
	
	private String memberName;
		
	private String memberAccount;
	
	private String memberPassword;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate memberBirthDate;
	
	private String memberAddress;
	
	@Column(nullable = false)
	private String memberEmail;
	
	@Column(nullable = true) //這裡有改
	private Integer points;
	
	private String lockStatus="正常"; //這裡有改
	
	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate registrationDate;
	
	
	
	  public MembersBean() {
	        
	    }



	public Integer getMemberId() {
		return memberId;
	}



	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}



	public byte[] getMemberPhoto() {
		return memberPhoto;
	}



	public void setMemberPhoto(byte[] memberPhoto) {
		this.memberPhoto = memberPhoto;
	}



	public String getMemberName() {
		return memberName;
	}



	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}



	public String getMemberPassword() {
		return memberPassword;
	}



	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}



	public LocalDate getMemberBirthDate() {
		return memberBirthDate;
	}



	public void setMemberBirthDate(LocalDate memberBirthDate) {
		this.memberBirthDate = memberBirthDate;
	}



	public String getMemberAccount() {
		return memberAccount;
	}



	public void setMemberAccount(String memberAccount) {
		this.memberAccount = memberAccount;
	}



	public String getMemberAddress() {
		return memberAddress;
	}



	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}



	public String getMemberEmail() {
		return memberEmail;
	}



	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}



	public Integer getPoints() {
		return points;
	}



	public void setPoints(Integer points) {
		this.points = points;
	}



	public String getLockStatus() {
		return lockStatus;
	}



	public void setLockStatus(String lockStatus) {
		this.lockStatus = lockStatus;
	}



	public LocalDate getRegistrationDate() {
		return registrationDate;
	}


	 public void setRegistrationDate(LocalDate registrationDate) {
	        this.registrationDate = registrationDate;
	    }

	@PrePersist
	public void prePersist() {
	      this.registrationDate = LocalDate.now(); // 在持久化之前自动设置当前日期
	 }

	 

}
