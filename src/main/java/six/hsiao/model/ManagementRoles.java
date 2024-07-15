package six.hsiao.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import six.yiting.model.StoresBean;

@Entity
@Table(name = "managementRoles")
public class ManagementRoles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "managementId", referencedColumnName = "managementId")
	private Management management;

	@ManyToOne
	@JoinColumn(name = "rolesId", referencedColumnName = "rolesId")
	private Roles roles;
	
	@ManyToOne
	@JoinColumn(name = "storeId",referencedColumnName = "storeId")
	private StoresBean store;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Management getManagement() {
		return management;
	}

	public void setManagement(Management management) {
		this.management = management;
	}

	public Roles getRoles() {
		return roles;
	}

	public void setRoles(Roles roles) {
		this.roles = roles;
	}

	public StoresBean getStore() {
		return store;
	}

	public void setStore(StoresBean store) {
		this.store = store;
	}
	
	

}