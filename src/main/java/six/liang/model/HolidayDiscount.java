package six.liang.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "holidayDiscount")
@Component
public class HolidayDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String discountName;  
    private String discountInfo;  
    private Integer isActive;

    @Lob
    private byte[] discountImage;  


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountInfo() {
        return discountInfo;
    }

    public void setDiscountInfo(String discountInfo) {
        this.discountInfo = discountInfo;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public byte[] getDiscountImage() {
        return discountImage;
    }

    public void setDiscountImage(byte[] discountImage) {
        this.discountImage = discountImage;
    }
}
