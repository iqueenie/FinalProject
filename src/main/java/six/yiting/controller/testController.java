package six.yiting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import six.yiting.model.StoresBean;
import six.yiting.service.GeocodingService;
import six.yiting.service.StoreService;

@Controller
public class testController {
	
	
	@Autowired
	private GeocodingService geocodingService;
	
	
	
	
	 @GetMapping("/address/{id}")
	    public String getAddressMap(@PathVariable Integer id, Model model) {
	        String address = geocodingService.getAddressFromDatabase(id);
	        if (address != null) {
	            String mapUrl = geocodingService.generateMapUrl(address);
	            model.addAttribute("mapUrl", mapUrl);
	        }
	        return "back/yiting/test";
	    }

}
