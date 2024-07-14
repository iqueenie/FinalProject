package six.yiting.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import six.yiting.model.StoresBean;
import six.yiting.model.StoresRepository;

import org.springframework.beans.factory.annotation.Value;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

@Service
public class GeocodingService {
	
	@Autowired
	private StoresRepository storesRepo;

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
	
	
	 @Value("${google.maps.api.key}")
	    private String apiKey;

	    public String getAddressFromDatabase(Integer id) {
	    	StoresBean store = findStoreById(id);
	    	String address = store.getCityNum()+store.getCity()+store.getArea()+store.getStreet();
	        return address;
	    }

	    public String generateMapUrl(String address) {
	        LatLng latLng = geocodeAddress(address);
	        if (latLng != null) {
	            String latitude = String.valueOf(latLng.lat);
	            String longitude = String.valueOf(latLng.lng);
	            return "https://www.google.com/maps/embed/v1/view?key=" + apiKey + "&center=" + latitude + "," + longitude + "&zoom=15";
	        } else {
	            return ""; // Handle failure case gracefully
	        }
	    }

	    public LatLng geocodeAddress(String address) {
	        GeoApiContext context = new GeoApiContext.Builder()
	                .apiKey(apiKey)
	                .build();

	        try {
	            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
	            if (results.length > 0) {
	                return results[0].geometry.location;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return null;
	    }

}

