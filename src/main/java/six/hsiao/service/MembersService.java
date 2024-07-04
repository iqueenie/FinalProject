package six.hsiao.service;

import java.util.List;

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
	
	public List<MembersBean> findAll(){
		return membersRepo.findAll();
	}
}
