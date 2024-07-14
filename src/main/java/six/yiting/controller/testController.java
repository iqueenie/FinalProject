package six.yiting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.google.maps.model.LatLng;

import six.yiting.service.GeocodingService;

@Controller
public class testController {
	
	
	@Autowired
	private GeocodingService geocodingService;
	
	
	
	
	 @GetMapping("/address/{id}")
	    public String getAddressMap(@PathVariable Integer id, Model model) {
	        String address = geocodingService.getAddressFromDatabase(id);
	        LatLng latLng= geocodingService.geocodeAddress(address);
	        if (latLng != null) {
	            model.addAttribute("latitude", latLng.lat);
	            model.addAttribute("longitude", latLng.lng);
	        }
	        return "back/yiting/test";
	    }

}
