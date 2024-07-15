package six.pinhong.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import six.hsiao.model.MembersBean;


@Entity @Table(name ="productReview")
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
    
	private Integer reviewContent; // 評論內容

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

	public Integer getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(Integer reviewContent) {
		this.reviewContent = reviewContent;
	}

	public ProductReview(MembersBean member, Product product, Integer stars, Integer reviewContent) {
		super();
		this.member = member;
		this.product = product;
		this.stars = stars;
		this.reviewContent = reviewContent;
	}

	public ProductReview() {

	}
}
