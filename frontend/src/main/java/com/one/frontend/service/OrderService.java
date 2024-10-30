package com.one.frontend.service;

import com.one.frontend.dto.DrawResultDto;
import com.one.frontend.ecpay.payment.integration.AllInOne;
import com.one.frontend.ecpay.payment.integration.domain.AioCheckOutALL;
import com.one.frontend.eenum.OrderStatus;
import com.one.frontend.model.*;
import com.one.frontend.repository.*;
import com.one.frontend.request.OrderQueryReq;
import com.one.frontend.request.PayCartRes;
import com.one.frontend.request.ReceiptReq;
import com.one.frontend.response.*;
import com.one.frontend.util.Md5;
import com.one.frontend.util.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
	@Autowired
	private UserTransactionRepository userTransactionRepository;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final CartItemService cartItemService;
	private final UserRepository userRepository;
	private final PaymentService paymentService;
	@Autowired
	private PrizeCartItemService prizeCartItemService;
	@Autowired
	private OrderTempMapper orderTempMapper;
	@Autowired
	private OrderDetailTempMapper orderDetailTempMapper;
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private StoreProductRepository storeProductRepository;
	@Autowired
	private ProductDetailRepository productDetailRepository;

	@Autowired
	private ShippingMethodRepository shippingMethodRepository;
	public String ecpayCheckout(Integer userId) {

		String uuId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);

		AllInOne all = new AllInOne("");

		AioCheckOutALL obj = new AioCheckOutALL();
		obj.setMerchantTradeNo(uuId);
		obj.setMerchantTradeDate("2017/01/01 08:05:23");
		obj.setTotalAmount("50");
		obj.setTradeDesc("test Description");
		obj.setItemName("TestItem");
		obj.setReturnURL("https://2217-111-248-73-184.ngrok-free.app/returnUrl");
		obj.setNeedExtraPaidInfo("N");
		obj.setCustomField1(String.valueOf(userId));
		String form = all.aioCheckOut(obj, null);
		System.out.println(form);
		return form;
	}

	public List<OrderRes> queryOrders(Long userId, OrderQueryReq req) {
		// 初始化查询参数
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);

		// 处理开始日期和结束日期
		LocalDateTime startDate = convertToLocalDateTime(req.getStartDate());
		LocalDateTime endDate = convertToLocalDateTime(req.getEndDate());

		if (startDate != null) {
			startDate = startDate.with(LocalTime.MIN);
		}
		if (endDate != null) {
			endDate = endDate.with(LocalTime.MAX);
		}

		params.put("startDate", startDate);
		params.put("endDate", endDate);

		// 查询订单
		List<OrderRes> ordersByDateRange = orderRepository.findOrdersByDateRange(params);

		// 为每个订单填充订单详情
		List<OrderRes> list = ordersByDateRange.stream()
				.map(order -> {
					System.out.println(order);
					order.setOrderDetails(orderDetailRepository.findOrderDetailsByOrderId(order.getId()));
					return order;
				})
				.collect(Collectors.toList());

		// 处理订单状态描述
		List<OrderRes> orderStatusDescriptions = list.stream()
				.map(order -> {
					String statusDescription;
					switch (order.getResultStatus()) {
						case "PREPARING_SHIPMENT":
							statusDescription = "訂單準備中";
							break;
						case "SHIPPED":
							statusDescription = "已發貨";
							break;
						case "NO_PAY":
							statusDescription = "未付款";
							break;
						default:
							statusDescription = "未知狀態";
							break;
					}
					// 设置状态描述
					order.setResultStatus(statusDescription);
					return order;
				})
				.collect(Collectors.toList());

		return orderStatusDescriptions;
	}


	private LocalDateTime convertToLocalDateTime(Date dateToConvert) {
	    return dateToConvert == null ? null : LocalDateTime.ofInstant(dateToConvert.toInstant(), ZoneId.systemDefault());
	}

	@Transactional(rollbackFor = Exception.class)
	public OrderPayRes createOrder(PayCartRes payCartRes, List<CartItem> cartItemList, Long userId) throws Exception {

		// 計算所有購物車商品的總價格
		BigDecimal totalProductAmount = cartItemList.stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		// 計算運費，根據運輸方式動態設置
		BigDecimal shippingCost = shippingMethodRepository.getShippingPrice(payCartRes.getShippingMethod());

		// 計算總金額，包括商品總價格和運費
		BigDecimal totalAmount = totalProductAmount.add(shippingCost);


		//取得用戶資訊
		UserRes userRes = userRepository.getUserById(userId);

		PaymentResponse paymentResponse = new PaymentResponse();
		if("1".equals(payCartRes.getPaymentMethod()) && payCartRes.getCardResult()){
			paymentResponse.setResult("1");
			paymentResponse.setOrderId(payCartRes.getOrderId());
			paymentResponse.setEPayAccount(String.valueOf(shippingCost));
		}else if("2".equals(payCartRes.getPaymentMethod())){
			PaymentRequest paymentRequest = new PaymentRequest();
			BigDecimal totalAmount2 = new BigDecimal(String.valueOf(totalAmount)); // 假设你的 totalAmount 是 BigDecimal
			int amountToSend = totalAmount2.setScale(0, BigDecimal.ROUND_DOWN).intValue(); // 去掉小数部分
			paymentRequest.setAmount(String.valueOf(amountToSend));
			paymentRequest.setBuyerName(payCartRes.getBillingName());
			paymentRequest.setBuyerTelm(payCartRes.getBillingPhone());
			paymentRequest.setBuyerMail(payCartRes.getBillingEmail());
			paymentRequest.setBuyerMemo("再來一抽備註");
			paymentResponse = paymentService.webATM(paymentRequest);
		}


		//paymentResponse result = 1 等於成功
		if("1".equals(paymentResponse.getResult())){

				// 創建訂單實體，這裡包含了支付和運輸方式、收貨人、賬單信息等字段
				Order orderEntity = Order.builder().userId(userId).orderNumber(paymentResponse.getOrderId()).totalAmount(totalAmount) // 總金額 =
						// 商品價格
						// + 運費
						.paymentMethod(payCartRes.getPaymentMethod()) // 從 PayCartRes 獲取支付方式
						.shippingCost(shippingCost) // 保存運費
						.shippingMethod(payCartRes.getShippingMethod()) // 從 PayCartRes 獲取運送方式
						.shippingName(payCartRes.getShippingName()) // 從 PayCartRes 獲取收貨人姓名
						.shippingEmail(payCartRes.getShippingEmail()) // 從 PayCartRes 獲取收貨人 Email
						.shippingPhone(payCartRes.getShippingPhone()) // 從 PayCartRes 獲取收貨人電話
						.shippingZipCode(payCartRes.getShippingZipCode()) // 從 PayCartRes 獲取收貨地址郵遞區號
						.shippingCity(payCartRes.getShippingCity()) // 從 PayCartRes 獲取收貨城市
						.shippingArea(payCartRes.getShippingArea()) // 從 PayCartRes 獲取收貨區域
						.shippingAddress(payCartRes.getShippingAddress()) // 從 PayCartRes 獲取收貨地址
						.billingName(payCartRes.getBillingName()) // 從 PayCartRes 獲取賬單姓名
						.billingEmail(payCartRes.getBillingEmail()) // 從 PayCartRes 獲取賬單 Email
						.billingPhone(payCartRes.getBillingPhone()) // 從 PayCartRes 獲取賬單電話
						.billingZipCode(payCartRes.getBillingZipCode()) // 從 PayCartRes 獲取賬單地址郵遞區號
						.billingCity(payCartRes.getBillingCity()) // 從 PayCartRes 獲取賬單城市
						.billingArea(payCartRes.getBillingArea()) // 從 PayCartRes 獲取賬單區域
						.billingAddress(payCartRes.getBillingAddress()) // 從 PayCartRes 獲取賬單地址
						.invoice(payCartRes.getInvoice()) // 從 PayCartRes 獲取發票信息
						.createdAt(LocalDateTime.now()).resultStatus(OrderStatus.PREPARING_SHIPMENT) // 訂單狀態設為準備發貨
						.paidAt(LocalDateTime.now()) // 假設已付款，更新付款時間
						.shopId(payCartRes.getShopId())
						.OPMode("711".equals(payCartRes.getShippingMethod()) ? "3" : "family".equals(payCartRes.getShippingMethod()) ? "1" : "0")
						.build();
			if(paymentResponse.getEPayAccount() != null){
				orderEntity.setBillNumber(paymentResponse.getEPayAccount());
			}

			if(payCartRes.getState() != null){
				orderEntity.setState("1");
				orderEntity.setDonationCode(payCartRes.getDonationCode());
			}

			PaymentResponse finalPaymentResponse = paymentResponse;
				if("1".equals(payCartRes.getPaymentMethod())) {
					// 插入訂單到資料庫
					orderEntity.setResultStatus(OrderStatus.PREPARING_SHIPMENT);
					orderRepository.insertOrder(orderEntity);

					// 根據訂單號查詢訂單ID
					Long orderId = orderRepository.getOrderIdByOrderNumber(paymentResponse.getOrderId());

					// 轉換購物車項目到訂單詳情並保存
					cartItemList.stream().map(cartItem -> mapCartItemToOrderDetail(cartItem, orderId , finalPaymentResponse.getEPayAccount())) // 映射購物車項目為訂單詳情
							.forEach(orderDetail -> orderDetailRepository.saveOrderDetail(orderDetail)); // 保存訂單詳情

					// 獲取所有購物車項的ID並移除
					List<Long> cartItemIds = cartItemList.stream().map(CartItem::getCartItemId).collect(Collectors.toList());

					// 移除購物車項
					cartItemService.removeCartItems(cartItemIds, cartItemList.get(0).getCartId());

					//訂單成立開立發票並且傳送至email
					ReceiptReq invoiceRequest = new ReceiptReq();
					if(payCartRes.getVehicle() != null){
						invoiceRequest.setOrderCode(payCartRes.getVehicle());
					}
					invoiceRequest.setEmail(userRes.getUsername());
					if(payCartRes.getState() != null){
						invoiceRequest.setState(1);
						invoiceRequest.setDonationCode(payCartRes.getDonationCode());
					}else{
						invoiceRequest.setState(0);
					}
					int amountToSend = totalAmount.setScale(0, BigDecimal.ROUND_DOWN).intValue(); // 去掉小数部分
					invoiceRequest.setTotalFee(String.valueOf(amountToSend));
					List<ReceiptReq.Item> items = new ArrayList<>();
					for(CartItem cartItem : cartItemList){
						ReceiptReq.Item item = new ReceiptReq.Item();
						StoreProduct byId = storeProductRepository.findById(cartItem.getStoreProductId());
						item.setName(byId.getProductName());
						item.setNumber(cartItem.getQuantity());
						item.setMoney(cartItem.getUnitPrice().intValue());
						items.add(item);
					}
					invoiceRequest.setItems(items);
					System.out.println("有到這");
					System.out.println(invoiceRequest);

					ResponseEntity<ReceiptRes> res = invoiceService.addB2CInvoice(invoiceRequest);
					System.out.println(res.getBody());
					ReceiptRes receiptRes = res.getBody();
					invoiceService.getInvoicePicture(receiptRes.getCode() , userId);
				}else if("2".equals(payCartRes.getPaymentMethod())){
					// 插入訂單到資料庫
					orderEntity.setResultStatus(OrderStatus.NO_PAY);
					orderRepository.insertOrder(orderEntity);

					// 根據訂單號查詢訂單ID
					Long orderId = orderRepository.getOrderIdByOrderNumber(paymentResponse.getOrderId());

					// 轉換購物車項目到訂單詳情並保存

					cartItemList.stream().map(cartItem -> mapCartItemToOrderDetail(cartItem, orderId , finalPaymentResponse.getEPayAccount())) // 映射購物車項目為訂單詳情
							.forEach(orderDetail -> orderDetailRepository.saveOrderDetail(orderDetail)); // 保存訂單詳情

					// 獲取所有購物車項的ID並移除
					List<Long> cartItemIds = cartItemList.stream().map(CartItem::getCartItemId).collect(Collectors.toList());

					// 移除購物車項
					cartItemService.removeCartItems(cartItemIds, cartItemList.get(0).getCartId());

				}

				this.recordConsume(userId , totalAmount);
		}else{
			throw new Exception("資料有錯" + paymentResponse.getRetMsg());
		}
		return new OrderPayRes(paymentResponse.getOrderId() , paymentResponse); // 返回訂單號
	}

	private void recordConsume(Long userId, BigDecimal amount) {
		userTransactionRepository.insertTransaction(userId, "CONSUME", amount);
	}

	@Transactional(rollbackFor = Exception.class)
	public OrderPayRes createPrizeOrder(PayCartRes payCartRes, List<PrizeCartItem> prizeCartItemList, Long userId) throws Exception {

		// 生成訂單號

		// 計算運費，根據運輸方式動態設置
		BigDecimal shippingCost =shippingMethodRepository.getShippingPrice(payCartRes.getShippingMethod());

		//取得用戶資訊
		UserRes userRes = userRepository.getUserById(userId);

		PaymentResponse paymentResponse = new PaymentResponse();
		if("1".equals(payCartRes.getPaymentMethod()) && payCartRes.getCardResult()){
			paymentResponse.setResult("1");
			paymentResponse.setOrderId(payCartRes.getOrderId());
			paymentResponse.setEPayAccount(String.valueOf(shippingCost));
		}else if("2".equals(payCartRes.getPaymentMethod())){
			PaymentRequest paymentRequest = new PaymentRequest();
			BigDecimal totalAmount2 = new BigDecimal(String.valueOf(shippingCost)); // 假设你的 totalAmount 是 BigDecimal
			int amountToSend = totalAmount2.setScale(0, BigDecimal.ROUND_DOWN).intValue(); // 去掉小数部分
			paymentRequest.setAmount(String.valueOf(amountToSend));
			paymentRequest.setBuyerName(payCartRes.getBillingName());
			paymentRequest.setBuyerTelm(payCartRes.getBillingPhone());
			paymentRequest.setBuyerMail(payCartRes.getBillingEmail());
			paymentRequest.setBuyerMemo("再來一抽備註");
			paymentResponse = paymentService.webATM(paymentRequest);
		}


		//paymentResponse result = 1 等於成功
		if("1".equals(paymentResponse.getResult())){
			// 創建訂單實體，這裡包含了支付和運輸方式、收貨人、賬單信息等字段
			Order orderEntity = Order.builder().userId(userId).orderNumber(paymentResponse.getOrderId()).totalAmount(shippingCost) // 總金額 =
					// 商品價格
					// + 運費
					.paymentMethod(payCartRes.getPaymentMethod()) // 從 PayCartRes 獲取支付方式
					.shippingCost(shippingCost) // 保存運費
					.shippingMethod(payCartRes.getShippingMethod()) // 從 PayCartRes 獲取運送方式
					.shippingName(payCartRes.getShippingName()) // 從 PayCartRes 獲取收貨人姓名
					.shippingEmail(payCartRes.getShippingEmail()) // 從 PayCartRes 獲取收貨人 Email
					.shippingPhone(payCartRes.getShippingPhone()) // 從 PayCartRes 獲取收貨人電話
					.shippingZipCode(payCartRes.getShippingZipCode()) // 從 PayCartRes 獲取收貨地址郵遞區號
					.shippingCity(payCartRes.getShippingCity()) // 從 PayCartRes 獲取收貨城市
					.shippingArea(payCartRes.getShippingArea()) // 從 PayCartRes 獲取收貨區域
					.shippingAddress(payCartRes.getShippingAddress()) // 從 PayCartRes 獲取收貨地址
					.billingName(payCartRes.getBillingName()) // 從 PayCartRes 獲取賬單姓名
					.billingEmail(payCartRes.getBillingEmail()) // 從 PayCartRes 獲取賬單 Email
					.billingPhone(payCartRes.getBillingPhone()) // 從 PayCartRes 獲取賬單電話
					.billingZipCode(payCartRes.getBillingZipCode()) // 從 PayCartRes 獲取賬單地址郵遞區號
					.billingCity(payCartRes.getBillingCity()) // 從 PayCartRes 獲取賬單城市
					.billingArea(payCartRes.getBillingArea()) // 從 PayCartRes 獲取賬單區域
					.billingAddress(payCartRes.getBillingAddress()) // 從 PayCartRes 獲取賬單地址
					.invoice(payCartRes.getInvoice()) // 從 PayCartRes 獲取發票信息
					.createdAt(LocalDateTime.now()).resultStatus(OrderStatus.PREPARING_SHIPMENT) // 訂單狀態設為準備發貨
					.paidAt(LocalDateTime.now()) // 假設已付款，更新付款時間
					.shopId(payCartRes.getShopId())
					.OPMode("711".equals(payCartRes.getShippingMethod()) ? "3" : "family".equals(payCartRes.getShippingMethod()) ? "1" : "0")
					.build();
			if(paymentResponse.getEPayAccount() != null){
				orderEntity.setBillNumber(paymentResponse.getEPayAccount());
			}

			if(payCartRes.getState() != null){
				orderEntity.setState("1");
				orderEntity.setDonationCode(payCartRes.getDonationCode());
			}
			PaymentResponse finalPaymentResponse = paymentResponse;
			if("1".equals(payCartRes.getPaymentMethod())) {
				// 插入訂單到資料庫
				orderEntity.setResultStatus(OrderStatus.PREPARING_SHIPMENT);
				orderRepository.insertOrder(orderEntity);

				// 根據訂單號查詢訂單ID
				Long orderId = orderRepository.getOrderIdByOrderNumber(paymentResponse.getOrderId());

				// 根據訂單號查詢訂單ID

				List<OrderDetail> orderDetails = prizeCartItemList.stream()
						.filter(Objects::nonNull)  // 過濾掉 null 元素
						.map(cartItem -> mapCartItemToPrizeOrderDetail(cartItem, orderId, shippingCost , finalPaymentResponse.getEPayAccount()))
						.filter(Objects::nonNull)  // 過濾掉映射結果為 null 的元素
						.collect(Collectors.toList());

				// 批量保存訂單詳情
				if (!orderDetails.isEmpty()) {
					orderDetailRepository.savePrizeOrderDetailBatch(orderDetails);
				}
				// 獲取所有購物車項的ID並移除
				List<Long> cartItemIds = prizeCartItemList.stream().map(PrizeCartItem::getPrizeCartItemId).collect(Collectors.toList());

				// 移除購物車項
				prizeCartItemService.removeCartItems(cartItemIds, prizeCartItemList.get(0).getCartId());

				//訂單成立開立發票並且傳送至email
				ReceiptReq invoiceRequest = new ReceiptReq();
				if(payCartRes.getVehicle() != null){
					invoiceRequest.setOrderCode(payCartRes.getVehicle());
				}
				invoiceRequest.setEmail(userRes.getUsername());
				if(payCartRes.getState() != null){
					invoiceRequest.setState(1);
					invoiceRequest.setDonationCode(payCartRes.getDonationCode());
				}else{
					invoiceRequest.setState(0);
				}
				int amountToSend = shippingCost.setScale(0, BigDecimal.ROUND_DOWN).intValue(); // 去掉小数部分
				invoiceRequest.setTotalFee(String.valueOf(amountToSend));
				List<ReceiptReq.Item> items = new ArrayList<>();
				for(PrizeCartItem cartItem : prizeCartItemList){
					ReceiptReq.Item item = new ReceiptReq.Item();
					ProductDetailRes byId = productDetailRepository.getProductDetailById(cartItem.getProductDetailId());
					item.setName(byId.getProductName());
					item.setNumber(cartItem.getQuantity());
					item.setMoney(shippingCost.intValue());
					items.add(item);
				}
				invoiceRequest.setItems(items);

				ResponseEntity<ReceiptRes> res = invoiceService.addB2CInvoice(invoiceRequest);
				ReceiptRes receiptRes = res.getBody();
				invoiceService.getInvoicePicture(receiptRes.getCode() , userId);
			}else if("2".equals(payCartRes.getPaymentMethod())){
				// 插入訂單到資料庫
				orderEntity.setResultStatus(OrderStatus.NO_PAY);
				orderRepository.insertOrder(orderEntity);

				// 根據訂單號查詢訂單ID
				Long orderId = orderRepository.getOrderIdByOrderNumber(paymentResponse.getOrderId());

				// 根據訂單號查詢訂單ID
				List<OrderDetail> orderDetails = prizeCartItemList.stream()
						.filter(Objects::nonNull)  // 過濾掉 null 元素
						.map(cartItem -> mapCartItemToPrizeOrderDetail(cartItem, orderId, shippingCost ,  finalPaymentResponse.getEPayAccount()))
						.filter(Objects::nonNull)  // 過濾掉映射結果為 null 的元素
						.collect(Collectors.toList());

				// 批量保存訂單詳情
				if (!orderDetails.isEmpty()) {
					orderDetailRepository.savePrizeOrderDetailBatch(orderDetails);
				}
				// 獲取所有購物車項的ID並移除
				List<Long> cartItemIds = prizeCartItemList.stream().map(PrizeCartItem::getPrizeCartItemId).collect(Collectors.toList());

				// 移除購物車項
				prizeCartItemService.removeCartItems(cartItemIds, prizeCartItemList.get(0).getCartId());
			}
			this.recordConsume(userId , shippingCost);
		}else{
			throw new Exception("資料有錯" + paymentResponse.getRetMsg());
		}
		return new OrderPayRes(paymentResponse.getOrderId() , paymentResponse); // 返回訂單號
	}

	private Order createOrderEntity(PayCartRes payCartRes, Long userId, String orderNumber, BigDecimal shippingCost) {
		return Order.builder()
				.userId(userId)
				.orderNumber(orderNumber)
				.totalAmount(shippingCost)
				.paymentMethod(payCartRes.getPaymentMethod())
				.shippingCost(shippingCost)
				.shippingMethod(payCartRes.getShippingMethod())
				.shippingName(payCartRes.getShippingName())
				// ... 其他字段 ...
				.createdAt(LocalDateTime.now())
				.resultStatus(OrderStatus.PREPARING_SHIPMENT)
				.paidAt(LocalDateTime.now())
				.build();
	}



	private void removeCartItems(List<PrizeCartItem> prizeCartItemList) {
		List<Long> prizeCartItemIds = prizeCartItemList.stream()
				.map(PrizeCartItem::getPrizeCartItemId)
				.collect(Collectors.toList());

		prizeCartItemService.removeCartItems(prizeCartItemIds, prizeCartItemList.get(0).getCartId());
	}


	public String genOrderNumber() {
		var orderDate = LocalDateTime.now();
		var orderDateStr = orderDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		var orderSerial = RandomUtils.genRandomNumbers(24);
		var orderNumber = String.format("%s%s", orderDateStr, orderSerial);
		return orderNumber;
	}

	public OrderDetail mapCartItemToOrderDetail(CartItem cartItem, Long orderId ,  String billNumber ) {
		BigDecimal totalPrice = cartItem.getUnitPrice().multiply(new BigDecimal(cartItem.getQuantity()));

		return OrderDetail.builder().orderId(orderId).storeProductId(cartItem.getStoreProductId())
				.quantity(cartItem.getQuantity()).unitPrice(cartItem.getUnitPrice()).totalPrice(totalPrice) // 新增
				.billNumber(billNumber)																						// totalPrice
				.build();
	}

	public OrderDetail mapCartItemToPrizeOrderDetail(PrizeCartItem cartItem, Long orderId , BigDecimal shippingCost , String billNumber) {

		return OrderDetail.builder().orderId(orderId).productDetailId(cartItem.getProductDetailId())
				.quantity(cartItem.getQuantity()).unitPrice(cartItem.getSliverPrice()).totalPrice(shippingCost)
				.billNumber(billNumber)// 新增
				.build();
	}

	public OrderRes getOrderByOrderNumber(Long userId, String orderNumber) {
		try {
			// 获取订单对象
			var order = orderRepository.getOrderByUserIdAndOrderNumber(userId, orderNumber);

			if (order != null) {
				// 获取订单详情并设置到订单对象中
				List<OrderDetailRes> orderDetails = orderDetailRepository.findOrderDetailsByOrderId(order.getId());
				order.setOrderDetails(orderDetails);

				// 获取订单状态并进行状态描述转换
				String statusDescription;
				String resultStatus = order.getResultStatus();

				if (resultStatus != null) {
					switch (resultStatus) {
						case "PREPARING_SHIPMENT":
							statusDescription = "訂單準備中";
							break;
						case "SHIPPED":
							statusDescription = "已發貨";
							break;
						case "NO_PAY":
							statusDescription = "未付款";
							break;
						default:
							statusDescription = "未知狀態";
							break;
					}
				} else {
					statusDescription = "狀態不可用";
				}

				// 将状态描述设置到 OrderRes 对象中
				order.setResultStatus(statusDescription);
			}

			return order;

		} catch (Exception e) {
			// 打印异常信息并记录日志
			System.err.println("Error retrieving order by order number: " + e.getMessage());
			// 可以抛出自定义异常或者返回默认值
			return null;  // 或者 throw new CustomException("Failed to retrieve order", e);
		}
	}


	public OrderRes getPrizeOrderByOrderNumber(Long userId, String orderNumber) {
		try {
			var order = orderRepository.getOrderByUserIdAndOrderNumber(userId, orderNumber);
			if (order != null) {
				List<OrderDetailRes> orderDetails = orderDetailRepository.findPrizeOrderDetailsByOrderId(order.getId());
				order.setOrderDetails(orderDetails);
			}

			return order;
		} catch (Exception e) {
			// 在这里处理异常，例如记录日志或抛出自定义异常
			System.err.println("Error retrieving order by order number: " + e.getMessage());
			// 您可以选择抛出自定义异常，或者返回一个默认值（如null）
			return null; // 或者 throw new CustomException("Failed to retrieve order", e);
		}
	}

	public List<DrawResultDto> queryDrawOrder(Long userId, OrderQueryReq req) {
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);

		LocalDateTime startDate = convertToLocalDateTime(req.getStartDate());
		LocalDateTime endDate = convertToLocalDateTime(req.getEndDate());

		if (startDate != null) {
			startDate = startDate.with(LocalTime.MIN);
		}

		if (endDate != null) {
			endDate = endDate.with(LocalTime.MAX);
		}

		params.put("startDate", startDate);
		params.put("endDate", endDate);

		List<DrawResultDto> results = orderRepository.queryDrawOrder(params.get("userId"),
				params.get("startDate"),
				params.get("endDate"));

		// 过滤掉 productName 和 imageUrls 为 null 的项
		if (results != null) {
			results = results.stream()
					.filter(result -> result.getProductName() != null && result.getImageUrls() != null)
					.collect(Collectors.toList());
		} else {
			results = new ArrayList<>();
		}

		return results;
	}


	private String express(String orderNumber , BigDecimal amount , PayCartRes payCartRes){
		String url = "https://logistics.gomypay.asia/LogisticsAPI.aspx";
		String md5 = "5E11B0983580ABDE" + orderNumber;
		String s = Md5.MD5(md5.toLowerCase());
		s = s.toUpperCase();
		// 获取当前日期
		LocalDate currentDate = LocalDate.now();
		// 定义日期格式
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		// 格式化日期
		String formattedDate = currentDate.format(formatter);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("Vendororder", orderNumber); // 客戶訂單編號
		params.add("mode", "C2C"); // 物流方式
		params.add("EshopId", "0038"); // 客戶代號
		params.add("StoreId", payCartRes.getShopId()); // 門市代號
		params.add("Amount", String.valueOf(amount)); // 交易金額
		params.add("ServiceType", "3"); // 服務型態代碼 //通路代號 1:全家 2:萊爾富3: 統一超商 4.OK 超商
		params.add("OrderAmount", String.valueOf(amount)); // 商品價值
		params.add("SenderName", "張三"); // 寄件人姓名
		params.add("SendMobilePhone", "0912345678"); // 寄件人手機電話
		params.add("ReceiverName", payCartRes.getShippingName()); // 取貨人姓名
		params.add("ReceiverMobilePhone", payCartRes.getShippingPhone()); // 取貨人手機電話
		params.add("OPMode", payCartRes.getOPMode()); // 通路代號
		params.add("Internetsite", "https://api.onemorelottery.tw:8081/logistics/callback"); // 接收狀態的網址
		params.add("ShipDate", formattedDate); // 出貨日期
		params.add("CHKMAC", s); // 檢查碼

		// 設定 Headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 設定為表單格式

		// 封裝請求
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		// 發送 POST 請求
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

		return response.getBody();
	}

}