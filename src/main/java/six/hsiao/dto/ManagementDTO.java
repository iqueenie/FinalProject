package six.hsiao.dto;

import java.io.Serializable;
import java.util.List;

public class ManagementDTO implements Serializable{
	
	
	private Long managementId;
	
	private String managementAccount;
	
	private List<String> roles;

	
	
	
	public Long getManagementId() {
		return managementId;
	}




	public void setManagementId(Long managementId) {
		this.managementId = managementId;
	}




	public String getManagementAccount() {
		return managementAccount;
	}




	public void setManagementAccount(String managementAccount) {
		this.managementAccount = managementAccount;
	}




	public List<String> getRoles() {
		return roles;
	}




	public void setRoles(List<String> roles) {
		this.roles = roles;
	}




	public ManagementDTO(String managementAccount, List<String> roles) {
		super();
		this.managementAccount = managementAccount;
		this.roles = roles;
	}




	public ManagementDTO(Long managementId, String managementAccount, List<String> roles) {
		super();
		this.managementId = managementId;
		this.managementAccount = managementAccount;
		this.roles = roles;
	}




	public ManagementDTO() {
		super();
	}
	
	
	
}
