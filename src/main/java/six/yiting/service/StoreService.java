package six.yiting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import six.pinhong.model.Product;
import six.pinhong.model.ProductRepository;
import six.yiting.model.StoresBean;
import six.yiting.model.StoresRepository;

@Service
public class StoreService {
	
	@Autowired
	private StoresRepository storesRepo;
	
	@Autowired ProductRepository productRepo;

	public List<StoresBean> findAllStores(){
		return storesRepo.findAll();
	}
	
	
	public StoresBean saveStore(StoresBean store) {
		return storesRepo.save(store);
	}
	
	public void deleteStore(Integer id) {
		storesRepo.deleteById(id);
	}
	
	public StoresBean findStoreById(Integer id) {
		
		Optional<StoresBean> optional = storesRepo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	
	public List<StoresBean> findStoreByCity(String city) {
		
	 List<StoresBean> byCity = storesRepo.findByCity(city);
	 
	 if(byCity!=null) {
		 return byCity;
	 }
	 return null;
		
	}
	
	public List<String> findAreaByCity(String city){
		List<String> areaByCity = storesRepo.findAreaByCity(city);
		 if(areaByCity!=null) {
			 return areaByCity;
		 }
		 return null;
			
		}
	

	public List<String> findStreetByArea(String city,String area){
		List<String> streetByArea = storesRepo.findStreetByArea(city, area);
		 if(streetByArea!=null) {
			 return streetByArea;
		 }
		 return null;
			
	}
	
	public long countStores(String city,String area) {
		return storesRepo.countByCityAndArea(city, area);
	}
	
	public long countStoresByStreet(String city,String area,String street) {
		return storesRepo.countByCityAndAreaAndStreet(city, area, street);
	}
	
	public long countByCityAndStreet(String city,String street) {
		return storesRepo.countByCityAndStreet(city, street);
	}
	
	public long countByWordName(String storeName) {
		return storesRepo.countByWordStoreName(storeName);
	}
	
	public List<StoresBean> findByCityAndArea(String city,String area){
		List<StoresBean> byCityAndArea = storesRepo.findByCityAndArea(city, area);
		if(byCityAndArea!=null) {
			 return byCityAndArea;
		 }
		 return null;
	}
	
	public List<StoresBean> findByCityAndAreaAndStreet(String city,String area,String street){
	 List<StoresBean> byCityAndAreaAndStreet = storesRepo.findByCityAndAreaAndStreet(city, area, street);
		if(byCityAndAreaAndStreet!=null) {
			 return byCityAndAreaAndStreet;
		 }
		 return null;
	}
	
	public List<StoresBean> findByCityAndStreet(String city,String street){
		List<StoresBean> byCityAndStreet = storesRepo.findByCityAndStreet(city, street);
		if(byCityAndStreet!=null) {	 
			return byCityAndStreet;
		 }
		 return null;
	}
	
	public String findByZip(String cityNum){
		String byZip = storesRepo.findByZip(cityNum);
		if(byZip!=null) {	 
			return byZip;
		 }
		 return null;
	}
	
	public List<StoresBean> findByName(String name) {
		List<StoresBean> storeByName = storesRepo.findStoreByName(name);
		if(storeByName!=null) {	 
			return storeByName;
		 }
		 return null;
	}
	
	
	public List<Product> findByType(){
		List<Product> allProduct = productRepo.findAll();
		List<Product> productType = new ArrayList<Product>();
		for(Product product: allProduct) {
			if(product.getProductType().equals("實體店")) {
				productType.add(product);
			}
		}
		return productType;
	}
	
	public List<Product> findByOtherType(){
		List<Product> allProduct = productRepo.findAll();
		List<Product> productType = new ArrayList<Product>();
		for(Product product: allProduct) {
			if(!product.getProductType().equals("實體店")) {
				productType.add(product);
			}
		}
		return productType;
	}
}

	



