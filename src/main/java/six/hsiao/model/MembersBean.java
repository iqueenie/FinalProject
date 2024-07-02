package six.hsiao.model;

import java.time.LocalDate;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="members")
@Component
public class MembersBean {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberId;
	
	@Column(nullable = true)//這裡有改
	private String memberPhoto;
	
	private String memberName;
	
	private String memberPassword;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate memberBirthDate;
	
	
	private String memberAccount;
	
	private String memberAddress;
	
	private String memberEmail;
	
	@Column(nullable = true) //這裡有改
	private Integer points;
	
	private String lockStatus="正常"; //這裡有改
	
	
	
	  public MembersBean() {
	        
	    }
	    
	    public MembersBean(String memberPhoto, String memberName, String memberPassword, LocalDate memberBirthDate,
	            String memberAccount, String memberAddress, String memberEmail, Integer points, String lockStatus) {
	        this.memberPhoto = memberPhoto;
	        this.memberName = memberName;
	        this.memberPassword = memberPassword;
	        this.memberBirthDate = memberBirthDate;
	        this.memberAccount = memberAccount;
	        this.memberAddress = memberAddress;
	        this.memberEmail = memberEmail;
	        this.points = points;
	        this.lockStatus = lockStatus;
	    }
	    
	    // Getters and Setters
	    public Integer getMemberId() {
	        return memberId;
	    }

	    public void setMemberId(Integer memberId) {
	        this.memberId = memberId;
	    }

	    public String getMemberPhoto() {
	        return memberPhoto;
	    }

	    public void setMemberPhoto(String memberPhoto) {
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

		public MembersBean(Integer memberId, String memberPhoto, String memberName, String memberPassword,
				LocalDate memberBirthDate, String memberAccount, String memberAddress, String memberEmail, Integer points,
				String lockStatus) {
			
			this.memberId = memberId;
			this.memberPhoto = memberPhoto;
			this.memberName = memberName;
			this.memberPassword = memberPassword;
			this.memberBirthDate = memberBirthDate;
			this.memberAccount = memberAccount;
			this.memberAddress = memberAddress;
			this.memberEmail = memberEmail;
			this.points = points;
			this.lockStatus = lockStatus;
		}


	
	


	

}
