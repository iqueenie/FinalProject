package six.hsiao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;



@Entity
@Table(name = "management")
public class Management {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "managementId")
	    private Long managementId;

	    @Column(name = "managementAccount", nullable = false, unique = true)
	    private String managementAccount;

	    @Column(name = "managementPassword", nullable = false)
	    private String managementPassword;
	    
	    @OneToMany(mappedBy = "management")
	    private List<ManagementRoles> managementRoles;
	    
    public Management() {
       
    }

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

    
    public String getManagementPassword() {
        return managementPassword;
    }

	public void setManagementPassword(String managementPassword) {
		this.managementPassword = managementPassword;
	}

	public List<ManagementRoles> getManagementRoles() {
		return managementRoles;
	}

	public void setManagementRoles(List<ManagementRoles> managementRoles) {
		this.managementRoles = managementRoles;
	}

   
}


