package six.liang.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.liang.model.HolidayDiscount;
import six.liang.model.HolidayDiscountRepository;
import six.pinhong.model.Product;

@Service
@Transactional
public class HolidayDiscountService {

    @Autowired
    private HolidayDiscountRepository holidayDiscountRepo;

    public List<HolidayDiscount> findAll() {
        return holidayDiscountRepo.findAll();
    }

    public void insert(HolidayDiscount discount) {
        holidayDiscountRepo.save(discount);
    }

    public void deleteById(Integer id) {
        holidayDiscountRepo.deleteById(id);
    }

    public void update(HolidayDiscount discount) {
        holidayDiscountRepo.save(discount);
    }

    public HolidayDiscount findById(Integer id) {
        return holidayDiscountRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("折扣信息不存在"));
    }

    public String getDiscountImageBase64(byte[] discountImage) {
        if (discountImage == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(discountImage);
    }

    public List<HolidayDiscount> findCurrentDiscounts() {
        return holidayDiscountRepo.findByIsActiveTrue();
    }

    public List<HolidayDiscount> findBySearchTermAndIsActive(String searchTerm) {
        return holidayDiscountRepo.findByDiscountNameContainingAndIsActiveTrue(searchTerm);
    }

    public HolidayDiscount findByDiscountName(String discountName) {
        return holidayDiscountRepo.findByDiscountName(discountName)
                .orElseThrow(() -> new RuntimeException("折扣信息不存在"));
    }
    
    
}