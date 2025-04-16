package six.pinhong.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
@Component
public class Product implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer productId; // 商品編號
	private String productName; // 商品名稱
	private String productType; // 商品類型
	private Integer productCost; // 成本
	private Integer productPrice; // 售價
	private Integer productExpirydate; // 效期

	@Column(length = 300) // 改這裡
	private String productDescription; // 描述, 詳細資訊
	private Integer productPublished; // 0 表示未上架，1 表示已上架
	private Integer productQuantity; // 商品數量

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
	private ProductImage productImage;
	
	// 商品評論
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
	private Set<ProductReview> reviews  = new HashSet<>(); 
	

	public Set<ProductReview> getReviews() {
		return reviews;
	}

	public void setReviews(Set<ProductReview> reviews) {
		this.reviews = reviews;
	}
	// -- 
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
	private Set<ProductFavorite> favorites  = new HashSet<>(); 
	
	public Set<ProductFavorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(Set<ProductFavorite> favorites) {
		this.favorites = favorites;
	}

	public Product(String productName, String productType, Integer productCost, Integer productPrice,
			Integer productExpirydate, String productDescription, Integer productPublished, Integer productQuantity) {
		super();
		this.productName = productName;
		this.productType = productType;
		this.productCost = productCost;
		this.productPrice = productPrice;
		this.productExpirydate = productExpirydate;
		this.productDescription = productDescription;
		this.productPublished = productPublished;
		this.productQuantity = productQuantity;
		
	}

	public Product() {

	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public Integer getProductCost() {
		return productCost;
	}

	public void setProductCost(Integer productCost) {
		this.productCost = productCost;
	}

	public Integer getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getProductExpirydate() {
		return productExpirydate;
	}

	public void setProductExpirydate(Integer productExpirydate) {
		this.productExpirydate = productExpirydate;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public Integer getProductPublished() {
		return productPublished;
	}

	public void setProductPublished(Integer productPublished) {
		this.productPublished = productPublished;
	}

	public ProductImage getProductImage() {
		return productImage;
	}

	public void setProductImage(ProductImage productImage) {
		this.productImage = productImage;
	}

	public Integer getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Product) {
			Product product =(Product) obj;
			boolean isEqual = productId.equals(product.getProductId());
			return isEqual;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(productId);
	}
	
}
