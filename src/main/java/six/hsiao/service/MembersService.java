package six.hsiao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;


@Service
@Transactional
public class MembersService {
	
	@Autowired
	private MembersRepository membersRepo;
	
	//找整張表
	public List<MembersBean> findAll(){
		return membersRepo.findAll();
	}
	
	//刪除
	public void deleteById(Integer id) {
		membersRepo.deleteById(id);
	}
	
	//查單筆
	public MembersBean findProductById(Integer id) {
		Optional<MembersBean> optional = membersRepo.findById(id);
		
		if (optional.isPresent()) {
			MembersBean members  = optional.get();
			return members;
		}
		
		return null;
	}
	
	//新增,更新
	public MembersBean insertMembers(MembersBean members) {
		return membersRepo.save(members);
	}
	
	//刪除
	public void deleteMembersById(Integer id) {
		membersRepo.deleteById(id);
	}
	
	
}
