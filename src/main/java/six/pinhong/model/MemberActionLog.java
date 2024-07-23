package six.pinhong.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "MemberActionLog")
public class MemberActionLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer actionLogId;

	@Column(name = "memberId", nullable = false)
	private Integer memberId;
	
	@Column(name = "memberAccount", nullable = false)
	private String memberAccount;
	
	@Column(name = "action", nullable = false)
	private String action;

	private Integer productId;
	
	private String productName;	

	@Column(name = "timestamp", nullable = false, updatable = false)
	private LocalDateTime timestamp;

	@PrePersist
	protected void onCreate() {
	     this.timestamp = LocalDateTime.now();
	}
		
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getActionLogId() {
		return actionLogId;
	}

	public void setActionLogId(Integer actionLogId) {
		this.actionLogId = actionLogId;
	}

	public String getMemberAccount() {
		return memberAccount;
	}

	public void setMemberAccount(String memberAccount) {
		this.memberAccount = memberAccount;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public MemberActionLog() {
		
	}

	public MemberActionLog(Integer memberId, String memberAccount, String action, Integer productId, String productName,
			LocalDateTime timestamp) {
		super();
		this.memberId = memberId;
		this.memberAccount = memberAccount;
		this.action = action;
		this.productId = productId;
		this.productName = productName;
		this.timestamp = timestamp;
	}
	
	
	
}
