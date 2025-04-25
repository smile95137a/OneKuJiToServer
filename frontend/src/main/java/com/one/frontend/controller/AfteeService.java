// AfteeService.java
package com.one.frontend.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.one.frontend.model.User;
import com.one.frontend.model.UserTransaction;
import com.one.frontend.repository.OrderDetailRepository;
import com.one.frontend.repository.OrderRepository;
import com.one.frontend.repository.UserRepository;
import com.one.frontend.repository.UserTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AfteeService {

	@Value("${aftee.key}")
	private String afteePubKey;

	@Value("${aftee.secret}")
	private String afteeSecret;

	@Value("${aftee.apiUrl}")
	private String afteeApiUrl;

	private final UserTransactionRepository userTransactionRepository;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final RestTemplate restTemplate;

	public Map<String, Object> generatePreRegisterPayload(String type, String orderNo, String returnUrl) {
		log.info("AFTEE 組裝 payload - type: {}, orderNo: {}", type, orderNo);

		return switch (type.toUpperCase()) {
		case "TOPUP" -> getTopupPayload(orderNo, returnUrl);
		case "MALL" -> getMallPayload(orderNo, returnUrl);
		case "PRIZE" -> getPrizePayload(orderNo, returnUrl);
		default -> throw new IllegalArgumentException("Unsupported AFTEE type: " + type);
		};
	}

	private Map<String, Object> getMallPayload(String orderNo, String returnUrl) {
			var order = orderRepository.getOrderByOrderNumber(orderNo);
			var orderDetailList = orderDetailRepository.findOrderDetailsByOrderId(Long.valueOf(order.getId()));
			User user = userRepository.getById(order.getUserId());
			String address = order.getShippingCity() + order.getShippingArea() + order.getShippingAddress();

			Map<String, Object> customer = new HashMap<>();
			customer.put("customer_name", order.getShippingName());
			customer.put("phone_number", order.getShippingPhone());
			customer.put("address", (address != null && !address.isEmpty()) ? address : "未填地址");
			customer.put("email", order.getShippingEmail());
			customer.put("additional_info_code", "FI");
			
			List<Map<String, Object>> items = orderDetailList.stream().map(detail -> {
				Map<String, Object> item = new HashMap<>();
				item.put("shop_item_id", detail.getProductId());
				item.put("item_name", detail.getProductName());
				item.put("item_category", detail.getProductDetailName() != null ? detail.getProductDetailName() : "商品盒");
				item.put("item_price", detail.getUnitPrice());
				item.put("item_count", detail.getQuantity());
				return item;
			}).collect(Collectors.toList());


			// 運費列為一個 item
			Map<String, Object> shippingItem = new HashMap<>();
			shippingItem.put("shop_item_id", "SHIPPING_FEE");
			shippingItem.put("item_name", "運費");
			shippingItem.put("item_category", "物流費用");
			shippingItem.put("item_price", order.getShippingCost());
			shippingItem.put("item_count", 1);
			items.add(shippingItem);

			Map<String, Object> payment = new HashMap<>();
			payment.put("amount", order.getTotalAmount());
			payment.put("shop_transaction_no", orderNo);
			payment.put("user_no", user.getUserUid());
			payment.put("sales_settled", false);
			payment.put("transaction_options", List.of());
			payment.put("description_trans", "");
			payment.put("customer", customer);
			payment.put("dest_customers", List.of());
			payment.put("items", items);
			payment.put("validation_datetime", "");
			payment.put("return_url", returnUrl);

			String checksum = AfteeChecksumUtils.generateChecksum(payment, afteeSecret);
			payment.put("checksum", checksum);

			Map<String, Object> result = new HashMap<>();
			result.put("pre_token", "");
			result.put("pub_key", afteePubKey);
			result.put("payment", payment);

			return result;
		}

	private Map<String, Object> getPrizePayload(String orderNo, String returnUrl) {
		var order = orderRepository.getOrderByOrderNumber(orderNo);
		var orderDetailList = orderDetailRepository.findOrderDetailsByOrderId(Long.valueOf(order.getId()));
		User user = userRepository.getById(order.getUserId());
		String address = order.getShippingCity() + order.getShippingArea() + order.getShippingAddress();

		Map<String, Object> customer = new HashMap<>();
		customer.put("customer_name", order.getShippingName());
		customer.put("phone_number", order.getShippingPhone());
		customer.put("address", (address != null && !address.isEmpty()) ? address : "未填地址");
		customer.put("email", order.getShippingEmail());
		customer.put("additional_info_code", "FI");
		
		List<Map<String, Object>> items = orderDetailList.stream().map(detail -> {
			Map<String, Object> item = new HashMap<>();
			item.put("shop_item_id", detail.getProductId());
			item.put("item_name", detail.getProductName());
			item.put("item_category", detail.getProductDetailName() != null ? detail.getProductDetailName() : "商品盒");
			item.put("item_price", detail.getUnitPrice());
			item.put("item_count", detail.getQuantity());
			return item;
		}).collect(Collectors.toList());


		// 運費列為一個 item
		Map<String, Object> shippingItem = new HashMap<>();
		shippingItem.put("shop_item_id", "SHIPPING_FEE");
		shippingItem.put("item_name", "運費");
		shippingItem.put("item_category", "物流費用");
		shippingItem.put("item_price", order.getShippingCost());
		shippingItem.put("item_count", 1);
		items.add(shippingItem);

		Map<String, Object> payment = new HashMap<>();
		payment.put("amount", order.getTotalAmount());
		payment.put("shop_transaction_no", orderNo);
		payment.put("user_no", user.getUserUid());
		payment.put("sales_settled", false);
		payment.put("transaction_options", List.of());
		payment.put("description_trans", "");
		payment.put("customer", customer);
		payment.put("dest_customers", List.of());
		payment.put("items", items);
		payment.put("validation_datetime", "");
		payment.put("return_url", returnUrl);

		String checksum = AfteeChecksumUtils.generateChecksum(payment, afteeSecret);
		payment.put("checksum", checksum);

		Map<String, Object> result = new HashMap<>();
		result.put("pre_token", "");
		result.put("pub_key", afteePubKey);
		result.put("payment", payment);

		return result;
	}

	private Map<String, Object> getTopupPayload(String orderNo, String returnUrl) {
		UserTransaction userTransaction = userTransactionRepository.findByOrderNumber2(orderNo);
		Long userId = userTransaction.getUserId();
		User user = userRepository.getById(userId);

		String userUid = user.getUserUid();
		String nickname = user.getNickname();
		String phone = user.getPhoneNumber();
		String email = user.getEmail();
		String address = user.getCity() + user.getArea() + user.getAddressName();
		BigDecimal amount = userTransaction.getAmount();

		Map<String, Object> customer = new HashMap<>();
		customer.put("customer_name", nickname);
		customer.put("phone_number", phone);
		customer.put("address", (address != null && !address.isEmpty()) ? address : "未填地址");
		customer.put("email", email);
		customer.put("additional_info_code", "FI");

		Map<String, Object> item = new HashMap<>();
		item.put("shop_item_id", "TOPUP");
		item.put("item_name", "儲值代幣");
		item.put("item_category", "商品");
		item.put("item_price", amount);
		item.put("item_count", 1);

		Map<String, Object> payment = new HashMap<>();
		payment.put("amount", amount);
		payment.put("shop_transaction_no", orderNo);
		payment.put("user_no", userUid);
		payment.put("sales_settled", true);
		payment.put("transaction_options", List.of());
		payment.put("description_trans", "");
		payment.put("customer", customer);
		payment.put("dest_customers", List.of());
		payment.put("items", List.of(item));
		payment.put("validation_datetime", "");
		payment.put("return_url", returnUrl);

		// Generate checksum
		String checksum = AfteeChecksumUtils.generateChecksum(payment, afteeSecret);
		payment.put("checksum", checksum);

		Map<String, Object> result = new HashMap<>();
		result.put("pre_token", "");
		result.put("pub_key", afteePubKey);
		result.put("payment", payment);

		return result;
	}

	public Map<String, Object> sendPreRegisterToAftee(Map<String, Object> preRegisterPayload) {
		String url = afteeApiUrl + "v1/transactions/pre_register";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(preRegisterPayload, headers);

		try {
			ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

			log.info("AFTEE 回應成功: {}", response.getBody());
			return response.getBody();
		} catch (Exception e) {
			log.error("AFTEE 發送失敗", e);
			throw new RuntimeException("AFTEE 請求失敗: " + e.getMessage(), e);
		}
	}
}