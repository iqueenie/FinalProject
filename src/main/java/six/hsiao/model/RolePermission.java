package six.hsiao.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rolePermission")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rolesId", referencedColumnName = "rolesId")
    private Roles roles;

    @ManyToOne
    @JoinColumn(name = "permissionId", referencedColumnName = "permissionId")
    private ActionPermission actionPermission;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Roles getRoles() {
		return roles;
	}

	public void setRoles(Roles roles) {
		this.roles = roles;
	}

	public ActionPermission getActionPermission() {
		return actionPermission;
	}

	public void setActionPermission(ActionPermission actionPermission) {
		this.actionPermission = actionPermission;
	}

   
}