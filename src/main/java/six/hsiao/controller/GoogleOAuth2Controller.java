package six.hsiao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import six.all.GoogleOAuth2Config;
import six.hsiao.model.MembersBean;
import six.hsiao.service.MembersService;

@Controller
public class GoogleOAuth2Controller {
	
	@Autowired
	private GoogleOAuth2Config googleOAuth2Config;
	
	@Autowired MembersService membersService;
	
	private final String scope = "https://www.googleapis.com/auth/userinfo.email";
	
	@GetMapping("/google-login")
	public String googleLogin(HttpServletResponse response) {
		String authUrl ="https://accounts.google.com/o/oauth2/v2/auth?" +
	    "client_id="+ googleOAuth2Config.getClientId() +
	    "&response_type=code" +
	    "&scope=openid%20email%20profile" +
	    "&redirect_uri=" + googleOAuth2Config.getRedirectUri() +
	    "&state=state";
		
		return "redirect:" + authUrl;
	}
	
	@GetMapping("/public/google-callback")
	public String googleCallback(@RequestParam(required = false) String code, Model model, HttpSession session) {
		
		if(code == null) {
			String authUrl ="https://accounts.google.com/o/oauth2/v2/auth?" +
					"client_id="+ googleOAuth2Config.getClientId() +
					"&redirect_uri=" + googleOAuth2Config.getRedirectUri() +
					"&scope=" + scope;
					return "redirect:"+ authUrl;
		}else {
			RestClient restClient = RestClient.create();
			
			try {
				// 準備 requestbody  // 拿 code 換 token
			String requestBody =  UriComponentsBuilder.newInstance()
				 .queryParam("code", code)
				 .queryParam("client_id", googleOAuth2Config.getClientId())
				 .queryParam("client_secret", googleOAuth2Config.getClientSecret())
				 .queryParam("redirect_uri", googleOAuth2Config.getRedirectUri())
				 .queryParam("grant_type", "authorization_code")
				 .build()
				 .getQuery();
			
			String credentials = restClient.post()
			.uri("https://oauth2.googleapis.com/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(requestBody)
			.retrieve()
			.body(String.class);
			
			System.out.println("credentials" + credentials);
			
			// json 字串傳換為 JsonNode (Java 物件)
			JsonNode jsonNode = new ObjectMapper().readTree(credentials);
			String accessToken = jsonNode.get("access_token").asText();
			String idToken = jsonNode.get("id_token").asText();
			
			// 拿到 token 後再去 google 請求一次，找 user
			String payloadResponse = restClient.get()
			.uri("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken)
			.header("Authorization", "Bearer "+ idToken)
			.retrieve()
			.body(String.class);
			
			System.out.println("payloadResponse" + payloadResponse);
			JsonNode userinfo = new ObjectMapper().readTree(payloadResponse);
			String loginUserEmail = userinfo.get("email").asText();
			
			boolean result = membersService.checkMemberAccount(loginUserEmail);
			
			if(result ) {
			
				MembersBean member = membersService.findByMemberAccount(loginUserEmail);
				session.setAttribute("loggedInMember",member);
			}else {
				
				MembersBean member = membersService.addMemberByGoogleLogin(loginUserEmail);
				session.setAttribute("loggedInMember",member);
			}
			
			
			
			}catch(Exception e){
				e.printStackTrace();
				
			}
			
			return "redirect:/public/front";
			
			
			
		
		
			
		}
	}
}

