package six.hsiao.model;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "management")

public class BackEndLoginBean {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)//這裡有改
	private int managementId;
	 
	private String managementAccount;
	 
		private String managementPassword;
	
	
	public int getManagementId() {
		return managementId;
	}


	public void setManagementId(int managementId) {
		this.managementId = managementId;
	}


	public String getManagementAccount() {
		return managementAccount;
	}


	public void setManagementAccount(String managementAccount) {
		this.managementAccount = managementAccount;
	}


	public String getManagementPassword() {
		return managementPassword;
	}


	public void setManagementPassword(String managementPassword) {
		this.managementPassword = managementPassword;
	}


	public BackEndLoginBean(String managementAccount, String managementPassword) {
		
		this.managementAccount = managementAccount;
		this.managementPassword = managementPassword;
	}


	public BackEndLoginBean() {
		
	}

}
