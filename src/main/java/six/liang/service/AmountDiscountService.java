package six.liang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import six.liang.model.AmountDiscount;
import six.liang.model.AmountDiscountRepository;


@Service
@Transactional
public class AmountDiscountService {
	
	@Autowired
	private AmountDiscountRepository amountDiscountRepo;

	public List<AmountDiscount> findAll(){
		return amountDiscountRepo.findAll();
	}
	

    public void insert(AmountDiscount discount) {
        amountDiscountRepo.save(discount);
    }
    
    public void deleteById(Integer id) {
		amountDiscountRepo.deleteById(id);
	}
    
}
