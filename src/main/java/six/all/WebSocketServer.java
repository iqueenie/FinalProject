//package six.all;
//
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.configurationprocessor.json.JSONArray;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.springframework.boot.web.servlet.server.Session;
//import org.springframework.stereotype.Component;
//
//import jakarta.websocket.OnOpen;
//import jakarta.websocket.server.PathParam;
//import jakarta.websocket.server.ServerEndpoint;
//
//@ServerEndpoint(value="/imserver/{members}")
//@Component
//public class WebSocketServer {
//
//	//創建通道  雙通道
//	private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
//	
//	
//	//紀錄客戶端 連接的人數
//	public static final Map<String,Session> sessionMap = new ConcurrentHashMap<>();
//	 
//	
//	@OnOpen
//	public void onOpen(Session session,@PathParam("member") String member) {
//		sessionMap.put(member,session);
//		log.info("有新用戶加入,member={},當前在線人數為:{}",member,sessionMap.size());
//		JSONObject result = new JSONObject();
//		JSONArray array = new JSONArray();
//		
//	}
//	
//}
