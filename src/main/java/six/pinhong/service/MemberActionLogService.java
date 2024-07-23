package six.pinhong.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.pinhong.model.MemberActionLog;
import six.pinhong.model.MemberActionLogRepository;
import six.pinhong.model.Product;
import six.pinhong.model.ProductRepository;

@Service
public class MemberActionLogService {

    @Autowired
    private MemberActionLogRepository memberActionLogRepo;
    
    @Autowired
    private MembersRepository membersRepo;
    
    @Autowired
    private ProductRepository productRepo;
    
    public void logAction(Integer memberId, String action, Integer productId) {
    	
    	String temp = "";
    	
    	Optional<MembersBean> optional = membersRepo.findById(memberId);
    	if (!optional.isEmpty()) {
    		// 我需要該會員的帳號    		
    		temp = optional.get().getMemberAccount();
		}
    	MemberActionLog log = new MemberActionLog();
        log.setMemberId(memberId);
        log.setMemberAccount(temp);
        log.setAction(action);
        log.setProductId(productId);
        
    	Optional<Product> optional2 = productRepo.findById(productId);
    	if (!optional2.isEmpty()) {
    		// 我需要該會員的瀏覽或收藏的產品名稱  
    		temp = optional2.get().getProductName();
		}
    	log.setProductName(temp);
    	
    	memberActionLogRepo.save(log);

    }
    
    public List<MemberActionLog> getAllLogs() {
        return memberActionLogRepo.findAll();
    }
    
}