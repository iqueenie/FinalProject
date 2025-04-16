package six.yiting.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import six.yiting.dto.ProductTypeNumberDto;
import six.yiting.model.StoresBean;
import six.pinhong.model.Product;





public interface InventoryRepository extends JpaRepository<InventoryBean, Integer>  {
	InventoryBean findByProductIdAndDeliveryDateAndStore(int productId,LocalDate deliveryDate,StoresBean store);
	List<InventoryBean> findByStoreAndProductId(StoresBean store, int productId);
	
	@Query(value="from InventoryBean WHERE store= :store AND expDate= :expDate")
	Page<InventoryBean> findByCityAndAreaPage(@Param(value = "store") StoresBean store, @Param(value = "expDate") LocalDate expDate,Pageable pgb);

	@Query(value="from InventoryBean WHERE store= :store AND expDate= :expDate AND product.productType=:productType")
	Page<InventoryBean> findByTypePage(@Param(value = "store") StoresBean store, @Param(value = "expDate") LocalDate expDate,@Param(value = "productType") String productType,Pageable pgb);
	
	@Query(value="SELECT DISTINCT product.productType from InventoryBean WHERE store= :store AND expDate= :expDate")
	List<String> findProductType(@Param(value = "store") StoresBean store, @Param(value = "expDate") LocalDate expDate);
	
	@Query(value="from InventoryBean WHERE store= :store AND expDate= :expDate AND product.productType=:productType")
	List<InventoryBean> findByType(@Param(value = "store") StoresBean store, @Param(value = "expDate") LocalDate expDate,@Param(value = "productType") String productType);
	
	List<InventoryBean> findByStoreAndExpDate(StoresBean store, LocalDate expDate);
	
	List<InventoryBean> findByStore(StoresBean store);
	
	InventoryBean findByStoreAndProductAndExpDate(StoresBean store, Product product, LocalDate expDate);
	
	InventoryBean findByStoreAndBuyCode(StoresBean store, String buyCode);



	
	
}


