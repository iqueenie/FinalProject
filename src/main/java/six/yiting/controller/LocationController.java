package six.yiting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.model.LatLng;

import six.yiting.service.GeocodingService;

@RestController
public class LocationController {

    @PostMapping("/location")
    public DistanceResponse calculateDistance(@RequestBody LocationRequest request) {
        double targetLat = 25.0330;  // 目标位置的纬度，例如台北 101
        double targetLon = 121.5654; // 目标位置的经度

        double distance = haversine(request.getLatitude(), request.getLongitude(), targetLat, targetLon);
        return new DistanceResponse(distance);
    }

    // 计算两点之间的距离（Haversine 公式）
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径，单位：公里
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 返回距离，单位：公里
    }
}

class LocationRequest {
    private double latitude;
    private double longitude;

    // Getters and setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

class DistanceResponse {
    private double distance;

    public DistanceResponse(double distance) {
        this.distance = distance;
    }

    // Getters and setters
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
