package six.hsiao.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.hsiao.model.MembersBean;
import six.hsiao.model.MembersRepository;
import six.pinhong.model.ProductImage;


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
	public MembersBean findByMemberId(Integer id) {
		Optional<MembersBean> optional = membersRepo.findById(id);
		
		if (optional.isPresent()) {
			MembersBean members  = optional.get();
			return members;
		}
		
		return null;
	}
	
	//新增
	public MembersBean insertMembers(MembersBean members) {
		return membersRepo.save(members);
	}
	
	  public MembersBean updateMembers(MembersBean updatedMember) {
	        
	        return membersRepo.save(updatedMember);
	    }
	
	
	//刪除
	public void deleteMembersById(Integer id) {
		membersRepo.deleteById(id);
	}
	
	public List<Object[]> countRegistrationsPerMonth() {
		LocalDate startDate = LocalDate.now().minusMonths(12).withDayOfMonth(1);
		return membersRepo.countRegistrationsPerMonth(startDate);
	}
		// 找圖片 BY ID
		public MembersBean findImageById(Integer id) {
			Optional<MembersBean> optional = membersRepo.findById(id);
			
			if (optional.isPresent()) {
				MembersBean MembersBeanImage = optional.get();
				return MembersBeanImage;
			}
			
			return null;
		}
	
		
		 public MembersBean findByMemberAccountAndMemberPassword(String memberAccount, String memberPassword) {
		        return membersRepo.findByMemberAccountAndMemberPassword(memberAccount, memberPassword);
		    }
		
		
		 public boolean checkMemberAccount(String memberAccount) {
				
				MembersBean dbUsers = membersRepo.findByMemberAccount(memberAccount);
				
				if(dbUsers == null) {
					return false;
				}
				
				return true;
					
			}
		
	public MembersBean findByMemberAccount(String memberAccount) {
	return	membersRepo.findByMemberAccount(memberAccount);
	}
	
	
	public MembersBean addMemberByGoogleLogin(String memberEmail) {
		MembersBean membersBean = new MembersBean();
		String memberName = memberEmail.split("@")[0];
		membersBean.setMemberName(memberName);
		membersBean.setMemberAccount(memberEmail);
		membersBean.setMemberEmail(memberEmail);
		membersBean.setGoogleLogin(Boolean.TRUE);
		membersBean.setPoints(0);
		
		
		return membersRepo.save(membersBean);
		
	}
	
}
