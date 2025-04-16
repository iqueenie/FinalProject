package six.hsiao.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.hsiao.dto.ManagementDTO;
import six.hsiao.model.ActionPermission;
import six.hsiao.model.ActionPermissionRepository;
import six.hsiao.model.Management;
import six.hsiao.model.ManagementRepository;
import six.hsiao.model.ManagementRoles;
import six.hsiao.model.ManagementRolesRepository;
import six.hsiao.model.Roles;
import six.hsiao.model.RolesRepository;

@Service
@Transactional
public class ManagementService {

	@Autowired
	private ManagementRepository managementRepository;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private ManagementRolesRepository managementRolesRepository;

//	@Autowired
//    private ActionPermissionRepository actionPermissionRepository;

	
	
	public boolean findManagement(String managementAccount, String managementPassword) {
       Management management = managementRepository.findByAccountAndPassword(managementAccount, managementPassword);
       return management != null;
	}
	
	
	
	//將找到的帳號密碼存取
	 public ManagementDTO findManagementDTO(String managementAccount, String managementPassword) {
	        Management management = managementRepository.findByAccountAndPassword(managementAccount, managementPassword);
	        if (management != null) {
	            return mapToDTO(management);
	        }
	        return null;
	    }
	 
	 
	 //登入時將取得登入 去判斷是甚麼角色
	 private ManagementDTO mapToDTO(Management management) {
	        ManagementDTO dto = new ManagementDTO();
	        dto.setManagementId(management.getManagementId());
	        dto.setManagementAccount(management.getManagementAccount());
	        dto.setRoles(extractRolesFromManagement(management));
	        return dto;
	    }
	 
	 
	 
	
	  private List<String> extractRolesFromManagement(Management management) {
	        return management.getManagementRoles().stream()                                  //取得管理員名稱
	                                              .map(managementRoles -> managementRoles.getRoles().getRolesName())
	                                              .collect(Collectors.toList());
	    }
	 
	 
	
	
	
	//找角色
	public Management findManagementWithRoles(String managementAccount, String managementPassword) {
        Management management = managementRepository.findByAccountAndPassword(managementAccount, managementPassword);
        if (management != null) {
            
            management.getManagementRoles().size(); 

            return management;
        }
        return null;
    }
	
	
	
	
	
	
	
	
	
	// 新增管理員帳號密碼 且密碼進行加密
	
	public void addManagement(String managementAccount, String managementPassword) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encryptedPassword = passwordEncoder.encode(managementPassword);

		// 創建管理員
		Management management = new Management();
		// 加入帳號
		management.setManagementAccount(managementAccount);
		// 加入 加密後的密碼
		management.setManagementPassword(encryptedPassword);
		// 新增 帳號密碼
		managementRepository.save(management);
	}

	
	public void addRole(String rolesName) {
		// 創建角色 例如 管理員 or 店長等等
		Roles role = new Roles();
		role.setRolesName(rolesName);

		 rolesRepository.save(role);
	}

	// 根據ManagementId 去賦予他的角色
	public void addManagementRoles(Long managementId, Long roleId) {
		Optional<Management> managementOptional = managementRepository.findById(managementId);
		
		//取得managementId 取得帳號密碼表ID
		if (managementOptional.isPresent()) {
			Management management = managementOptional.get();
			//取得roleId 角色表ID
			Optional<Roles> roleOptional = rolesRepository.findById(roleId);
			if (roleOptional.isPresent()) {
				Roles role = roleOptional.get();
				
				//將輸入的帳號密碼ID 以及角色ID 設置managementRoles表中
				ManagementRoles managementRoles = new ManagementRoles();
				managementRoles.setManagement(management);
				managementRoles.setRoles(role);
				
				//執行新增設置的資料
				managementRolesRepository.save(managementRoles);
			} else {
				throw new RuntimeException("沒有這個角色他的id為: " + roleId);
			}
		} else {
			throw new RuntimeException("沒有這組帳號密碼他的id為: " + managementId);
		}
	}

	// 暫時沒有這個東西,等我做完security之後,有想法可以運用在我們的專題的哪裡,要討論過後才會有
//	 public ActionPermission addActionPermission(String permissionName) {
//	       	
//	        ActionPermission actionPermission = new ActionPermission();
//	        //設定權限 他能做甚麼事
//	        actionPermission.setPermission(permissionName);
//
//	       
//	        return actionPermissionRepository.save(actionPermission);
//	    }

}
