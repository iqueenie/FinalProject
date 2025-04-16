package six.liang.service;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.liang.model.AmountDiscount;
import six.liang.model.AmountDiscountRepository;
import six.liang.model.HolidayDiscount;

@Service
@Transactional
public class AmountDiscountService {

    @Autowired
    private AmountDiscountRepository amountDiscountRepo;

    public List<AmountDiscount> findAll() {
        return amountDiscountRepo.findAll();
    }

    public void insert(AmountDiscount discount) {
        amountDiscountRepo.save(discount);
    }

    public void deleteById(Integer id) {
        amountDiscountRepo.deleteById(id);
    }

    public void update(AmountDiscount discount) {
        amountDiscountRepo.save(discount);
    }

    public AmountDiscount findById(Integer id) {
        return amountDiscountRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("折扣信息不存在"));
    }

    public String getDiscountImageBase64(byte[] discountImage) {
        if (discountImage == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(discountImage);
    }

    
    public List<AmountDiscount> findCurrentDiscounts() {
        LocalDate currentDate = LocalDate.now();
        return amountDiscountRepo.findByStartDateBeforeAndEndDateAfterAndIsActive(currentDate, currentDate, 1);
    }

    public List<AmountDiscount> findBySearchTermAndIsActive(String searchTerm) {
        return amountDiscountRepo.findByDiscountDescriptionContainingAndIsActiveTrue(searchTerm);
    }
    
    public AmountDiscount findByDiscountName(String discountName) {
        return amountDiscountRepo.findByDiscountName(discountName)
                .orElseThrow(() -> new RuntimeException("折扣信息不存在"));
    }
    
    
}
