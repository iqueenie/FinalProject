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
import jakarta.persistence.UniqueConstraint;
import six.hsiao.model.MembersBean;

@Entity
@Table(name ="ProductFavorite", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"memberId", "productId"})
})
@Component
public class ProductFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favoriteId; // 主键

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private MembersBean member; // MembersBean的PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product; // 商品编号

    public Integer getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Integer favoriteId) {
        this.favoriteId = favoriteId;
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

    public ProductFavorite(MembersBean member, Product product) {
        this.member = member;
        this.product = product;
    }

    public ProductFavorite() {
    }
}