package six.all;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import six.sunny.model.GroupMember;
import six.sunny.service.GroupMemberService;

@Service
public class LinePayService {

	@Value("${line.pay.channel.id}")
	private String CHANNEL_ID;

	@Value("${line.pay.channel.secret}")
	private String CHANNEL_SECRET;

	@Value("${line.pay.api.url}")
	private String API_URL;

	@Autowired
	private GroupMemberService groupMemberService;

	private ObjectMapper objectMapper = new ObjectMapper();

	public static String encrypt(final String keys, final String data) {
		return toBase64String(
				HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, keys.getBytes()).doFinal(data.getBytes()));
	}

	public static String toBase64String(byte[] bytes) {
		byte[] byteArray = Base64.encodeBase64(bytes);
		return new String(byteArray);
	}

	public JsonNode sendPost(Integer id) {

		String requestUri = "/v3/payments/request";
		String nonce = UUID.randomUUID().toString();
		String form = generateForm(id);
		String signature = encrypt(CHANNEL_SECRET, CHANNEL_SECRET + requestUri + form + nonce);
		String httpsUrl = "https://sandbox-api-pay.line.me/v3/payments/request";

		RestTemplate restTemplate = new RestTemplate();

		JsonNode json = null;

		//       Post Headers 設定

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);

		headers.add("X-LINE-ChannelId", CHANNEL_ID);

		headers.add("X-LINE-Authorization-Nonce", nonce);

		headers.add("X-LINE-Authorization", signature);

		HttpEntity<String> request = new HttpEntity<String>(form, headers);

		String responsebody = restTemplate.postForObject(httpsUrl, request, String.class);

		ObjectMapper mapper = new ObjectMapper();

		try {
			json = mapper.readTree(responsebody);

			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;

	}

	public String generateForm(Integer id) {

		GroupMember groupMember = groupMemberService.findById(id);

		Map<String, Object> form = new HashMap<>();
		form.put("amount", groupMember.getTotal());
		form.put("currency", "TWD");
		form.put("orderId", "TestGroupbuye" + groupMember.getGroupMemberId());
		form.put("packages",
				Arrays.asList(Map.of("id", "groupbuyPackagese" + groupMember.getGroupMemberId(),
						"name", groupMember.getGroupBuy().getStoreName(),
						"amount", groupMember.getTotal(),
						"products",
							Arrays.asList(Map.of(
									"id", groupMember.getGroupBuy().getProductId().toString(), 
									"name", groupMember.getGroupBuy().getProductName(), 
									"imageUrl", "",
									"quantity", groupMember.getQuantity(), 
									"price", groupMember.getGroupBuy().getPrice())))));
		form.put("redirectUrls",
					Map.of(
							"confirmUrl", "http://localhost:8080/FinalProject/group-buy-line-pay/confirm",
							"cancelUrl", "http://localhost:8080/FinalProject/public/front/group-member-orders"));

		try {
			return objectMapper.writeValueAsString(form);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	暫時無法使用
	public boolean confirmTransaction(String transactionId, String orderId) {

		int id = Integer.parseInt(orderId.replace("TestGroupbuyd", ""));
		GroupMember groupMember = groupMemberService.findById(id);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("amount", groupMember.getTotal());
		requestBody.put("currency", "TWD");

		RestTemplate restTemplate = new RestTemplate();

		try {
			String requestUri = "https://sandbox-api-pay.line.me/v3/payments/" + transactionId + "/confirm";
			String nonce = UUID.randomUUID().toString();
			String requestBodyString = objectMapper.writeValueAsString(requestBody);
			String signature = encrypt(CHANNEL_SECRET, CHANNEL_SECRET + requestUri + requestBodyString + nonce);

			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(MediaType.APPLICATION_JSON);

			headers.add("X-LINE-ChannelId", CHANNEL_ID);

			headers.add("X-LINE-Authorization-Nonce", nonce);

			headers.add("X-LINE-Authorization", signature);

			HttpEntity<String> request = new HttpEntity<String>(requestBodyString, headers);

			String response = restTemplate.postForObject(requestUri, request, String.class);

			System.out.println("完整響應: " + response);

			JsonNode responseBody = objectMapper.readTree(response);
			String returnCode = responseBody.path("returnCode").asText();
			if ("0000".equals(returnCode)) {
				return true;
			} else {
				System.out.println("LINE Pay API 返回錯誤: " + responseBody.toString());
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
