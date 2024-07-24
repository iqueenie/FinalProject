package six.queenie.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    private final Map<Integer, String> cache = new ConcurrentHashMap<>();

    public void put(Integer orderId, String logisticsID) {
        cache.put(orderId, logisticsID);
    }

    public String get(Integer orderId) {
        return cache.get(orderId);
    }

    public void remove(Integer orderId) {
        cache.remove(orderId);
    }
}
