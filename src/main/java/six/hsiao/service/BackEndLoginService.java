package six.hsiao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.hsiao.model.BackEndLoginBean;
import six.hsiao.model.BackEndLoginRepository;

@Service
@Transactional
public class BackEndLoginService {
    
    @Autowired
    private BackEndLoginRepository backEndLoginRepo;
    
    public boolean findUserService(String managementAccount, String managementPassword) {
    	 BackEndLoginBean user = backEndLoginRepo.findByAccountAndPassword(managementAccount, managementPassword);
    	 return user != null;
    }

	
}