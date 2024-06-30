package six.yiting.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import six.hsiao.model.MembersBean;

@Entity @Table(name="storeLike")
public class StoreLikeBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id @Column(name="LIKEID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int likeId;
	
	@Column(name="STOREID",insertable=false,updatable=false)
	private int storeId;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="STOREID")
	private StoresBean store;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="USERID")
	private MembersBean member;
	
	
	
	public int getLikeId() {
		return likeId;
	}
	public void setLikeId(int likeId) {
		this.likeId = likeId;
	}
	
	public StoresBean getStore() {
		return store;
	}
	public void setStore(StoresBean store) {
		this.store = store;
	}
	public MembersBean getMember() {
		return member;
	}
	public void setMember(MembersBean member) {
		this.member = member;
	}
	

	
}