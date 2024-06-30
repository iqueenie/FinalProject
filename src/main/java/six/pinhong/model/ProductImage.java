package six.pinhong.model;

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.GenericGenerators;
import org.hibernate.annotations.Parameter;
import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity @Table(name ="productImage")
@Component
public class ProductImage implements Serializable{
	
	@SuppressWarnings("deprecation")
	@GenericGenerator(name="generator", strategy = "foreign", parameters = @Parameter(name="property", value = "product"))
	@Id @GeneratedValue(generator = "generator")
	private Integer productId; // 商品編號
    private String imageUrl; // 圖片 URL
    
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Product product;
    
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public ProductImage() {

	}
	public ProductImage(String imageUrl) {
		super();
		this.imageUrl = imageUrl;
	}
	public ProductImage(Integer productId, String imageUrl) {
		super();
		this.productId = productId;
		this.imageUrl = imageUrl;
	}
    
    
}
