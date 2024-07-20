package six.hsiao.model;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import six.pinhong.model.ProductFavorite;
import six.pinhong.model.ProductReview;

@Entity
@Table(name = "members")
public class MembersBean {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberId;
	
	@Lob
	@Column(nullable = true)//這裡有改
	private byte[] memberPhoto;
	
	private String memberName;
		
	private String memberAccount;
	
	private String memberPassword;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate memberBirthDate;
	
	private String memberAddress;
	
	@Column(nullable = true)
	private String memberEmail;
	
	@Column(nullable = true) //這裡有改
	private Integer points;
	
	private String lockStatus="正常"; //這裡有改
	
	@Column(nullable = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate registrationDate;
	
	private String resetToken;
	
	
	private Boolean googleLogin;
	
	// 品宏的評論	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
	private Set<ProductReview> reviews = new HashSet<>();
	
	public Set<ProductReview> getReviews() {
		return reviews;
	}
	  
	public void setReviews(Set<ProductReview> reviews) {
		this.reviews = reviews;
	}
	
	// -----
	
	// 品宏的收藏
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
	private Set<ProductFavorite> favorites = new HashSet<>();
	
	public Set<ProductFavorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(Set<ProductFavorite> favorites) {
		this.favorites = favorites;
	}

	//---------------

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
	      this.registrationDate = LocalDate.now(); 
	 }

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public Boolean getGoogleLogin() {
		return googleLogin;
	}

	public void setGoogleLogin(Boolean googleLogin) {
		this.googleLogin = googleLogin;
	}

	 

	public boolean isLocked() {
        return lockStatus != null && lockStatus.startsWith("locked:");
    }


    public LocalDate getLockDate() {
        if (isLocked()) {
            String timestampStr = lockStatus.substring("locked:".length());
            return LocalDate.parse(timestampStr);
        }
        return null;
    }

    public void lock(LocalDate lockDate) {
        this.lockStatus = "locked:" + lockDate.toString();
    }

    public void unlock() {
        this.lockStatus = "正常";
    }
}


