package com.one.frontend.controller;

import com.one.frontend.config.security.CustomUserDetails;
import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.repository.UserRepository;
import com.one.frontend.request.OrderQueryReq;
import com.one.frontend.request.PayCartRes;
import com.one.frontend.service.*;
import com.one.frontend.util.ResponseUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final OrderDetailService orderDetailService;
	private final UserRepository userRepository;
	private final CartItemService cartItemService;
	private final CartService cartService;
	@Autowired
	private PrizeCartService prizeCartService;

	@Autowired
	private PrizeCartItemService prizeCartItemService;

//	// 根据ID获取订单
//	@GetMapping("/{userUid}")
//	public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable String userUid) {
//		Order order = orderService.getOrderById(userUid);
//
//		if (order == null) {
//			ApiResponse<Order> response = ResponseUtils.failure(404, "沒有此ID", null);
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//		}
//
//		ApiResponse<Order> response = ResponseUtils.success(200, null, order);
//		return ResponseEntity.ok(response);
//	}
//	
//	
	@PostMapping("/queryOrder")
	public ResponseEntity<?> queryOrder(@RequestBody OrderQueryReq req) {
		try {
			CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
			if (userDetails == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			var userId = userDetails.getId();
			var list = orderService.queryOrders(userId,req);
			var res = ResponseUtils.success(200, null, list);
			return ResponseEntity.ok(res);
		}catch (Exception e){
			e.printStackTrace();
		}
	return null;
	}

	@PostMapping("/queryDrawOrder")
	public ResponseEntity<?> queryDrawOrder(@RequestBody OrderQueryReq req) {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		var userId = userDetails.getId();
		var list = orderService.queryDrawOrder(userId,req);
		var res = ResponseUtils.success(200, null, list);
		return ResponseEntity.ok(res);
	}

	@PostMapping("/storeProduct/pay")
	public ResponseEntity<?> payCartItem(@RequestBody PayCartRes payCartRes) throws Exception {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
		var userId = userDetails.getId();
		try {
			Long cartId = cartService.getCartIdByUserId(userId);

			if (cartId == null) {
				var response = ResponseUtils.failure(999, "無法找到購物車，請稍後重試", false);
				return ResponseEntity.ok(response);
			}

			var cartItemList = cartItemService.findByCartIdAndCartItemList(cartId, payCartRes.getCartItemIds());
			var orderNumber = orderService.createOrder(payCartRes, cartItemList, userId);

			if(orderNumber == null){
				ResponseEntity.ok(ResponseUtils.failure(200, "訂單失敗,商品已無庫存", null));
			}

			return ResponseEntity.ok(ResponseUtils.success(200, "支付成功，订单已创建", orderNumber));
		}catch (Exception e){
			e.printStackTrace();
		}
        return null;
    }

	@PostMapping("/product/pay")
	public ResponseEntity<?> payPrizeCartItem(@RequestBody PayCartRes payCartRes) {
		try{
			CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
			var userId = userDetails.getId();
			Long cartId = prizeCartService.getCartIdByUserId(userId);

			if (cartId == null) {
				var response = ResponseUtils.failure(999, "無法找到購物車，請稍後重試", false);
				return ResponseEntity.ok(response);
			}

			var prizeCartItemList = prizeCartItemService.findByCartIdAndCartItemList(cartId, payCartRes.getPrizeCartItemIds());
			var orderNumber = orderService.createPrizeOrder(payCartRes, prizeCartItemList, userId);

			return ResponseEntity.ok(ResponseUtils.success(200, "支付成功，订单已创建", orderNumber));
		}catch (Exception e){
			e.printStackTrace();
		}
		return ResponseEntity.ok(ResponseUtils.success(500, "失敗", null));
	}

	@GetMapping("/storeProduct/{orderNumber}")
	public ResponseEntity<?> getStoreProductOrderById(@PathVariable String orderNumber) {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
		var userId = userDetails.getId();
		var res = orderService.getOrderByOrderNumber(userId, orderNumber);
		return ResponseEntity.ok(ResponseUtils.success(200, null, res));
	}

	@GetMapping("/productDetail/{orderNumber}")
	public ResponseEntity<?> getStorePrizeProductOrderById(@PathVariable String orderNumber) {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
		var userId = userDetails.getId();
		var res = orderService.getPrizeOrderByOrderNumber(userId, orderNumber);
		return ResponseEntity.ok(ResponseUtils.success(200, null, res));
	}

	@PostMapping("/ecpayCheckout")
	public String ecpayCheckout(@RequestBody Integer userId) {

		return orderService.ecpayCheckout(userId);
	}

	@PostMapping(value = "/returnUrl", consumes = "application/x-www-form-urlencoded")
	public String handleEcpayNotification(@RequestParam Map<String, String> paymentData, HttpServletResponse response) {
		// 打印接收到的数据，实际部署时应该移除或使用日志记录
		System.out.println("Received payment data: " + paymentData);

		// 检查返回的支付状态码
		if ("1".equals(paymentData.get("RtnCode"))) {
			// 付款成功
			String merchantTradeNo = paymentData.get("MerchantTradeNo");
			String tradeAmt = paymentData.get("TradeAmt");
			String memberId = paymentData.get("CustomField1");
			updateCustomerAccount(merchantTradeNo, tradeAmt, memberId);

			// 返回 1|OK 告知绿界已成功接收
			response.setStatus(HttpServletResponse.SC_OK);
			return "1|OK";
		} else {
			// 处理支付失败的情况
			return handlePaymentFailure(paymentData);
		}
	}

	private void updateCustomerAccount(String merchantTradeNo, String tradeAmt, String memberId) {

		userRepository.updateAccoutnt(tradeAmt, memberId);
		// 实现更新账户余额逻辑
		System.out.println("Update account balance for trade number: " + merchantTradeNo + " with amount: " + tradeAmt);
	}

	private String handlePaymentFailure(Map<String, String> paymentData) {
		// 实现失败处理逻辑
		System.out.println("Payment failed with message: " + paymentData.get("RtnMsg"));
		return "0|Error";
	}
}