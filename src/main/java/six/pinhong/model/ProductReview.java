package six.pinhong.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import six.hsiao.model.MembersBean;


@Entity
@Table(name ="productReview", uniqueConstraints = {
@UniqueConstraint(columnNames = {"memberId", "productId"})})
@Component
public class ProductReview {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reviewId; // 評論編號
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
	private MembersBean member; // MembersBean的PK
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
	private Product product; // 商品編號
    
	private Integer stars; // 評論星數
    
	@Column(length = 300)
	private String reviewContent; // 評論內容
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 若要在 thymeleaf 強制使用本格式，需雙層大括號
	@Temporal(TemporalType.TIMESTAMP)
	private Date reviewTime; // 評論時間
	
	public Integer getReviewId() {
		return reviewId;
	}



	public void setReviewId(Integer reviewId) {
		this.reviewId = reviewId;
	}



	public MembersBean getMember() {
		return member;
	}



	public void setMember(MembersBean member) {
		this.member = member;
	}



	public Product getProduct() {
		return product;
	}



	public void setProduct(Product product) {
		this.product = product;
	}



	public Integer getStars() {
		return stars;
	}



	public void setStars(Integer stars) {
		this.stars = stars;
	}



	public String getReviewContent() {
		return reviewContent;
	}



	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}



	public Date getReviewTime() {
		return reviewTime;
	}



	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}

	public ProductReview(MembersBean member, Product product, Integer stars, String reviewContent, Date reviewTime) {
		super();
		this.member = member;
		this.product = product;
		this.stars = stars;
		this.reviewContent = reviewContent;
		this.reviewTime = reviewTime;
	}

	public ProductReview() {

	}
}
