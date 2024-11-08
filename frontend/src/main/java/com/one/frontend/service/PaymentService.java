package com.one.frontend.service;

import com.google.gson.Gson;
import com.one.frontend.dto.CreditDto;
import com.one.frontend.model.*;
import com.one.frontend.repository.*;
import com.one.frontend.request.ReceiptReq;
import com.one.frontend.response.*;
import jakarta.mail.MessagingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private UserTransactionRepository userTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderTempMapper orderTempMapper;

    @Autowired
    private OrderDetailTempMapper orderDetailTempMapper;

    @Autowired
    private UserRewardRepository userRewardRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private PaymentResponseMapper paymentResponseMapper;

    @Autowired
    private InvoiceService invoiceService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String CUSTOMERID = "B82FD0DF7DE03FC702DEC35A2446E469";
    private final String STRCHECK = "d0q2mo1729enisehzolmhdwhkac38itb";

    public PaymentResponse creditCard(PaymentRequest paymentRequest) {

       String url = "https://n.gomypay.asia/ShuntClass.aspx";  //正式
        // String url = "https://n.gomypay.asia/TestShuntClass.aspx";  //測試

        PaymentRequest req = PaymentRequest.builder()
                .sendType("0".trim())  // 傳送型態，寫死去除空白
                .payModeNo("2".trim())  // 付款模式，寫死去除空白
                .customerId(CUSTOMERID.trim())  // 商店代號，去除前後空白
                .amount(paymentRequest.getAmount())  // 交易金額，數值無需去空白
                .transCode("00".trim())  // 交易類別，寫死去除空白
                .buyerName(paymentRequest.getBuyerName().trim().replaceAll("\\s+", " "))  // 消費者姓名，去除前後空白
                .buyerTelm(paymentRequest.getBuyerTelm().trim())  // 消費者手機，去除前後空白
                .buyerMail(paymentRequest.getBuyerMail().trim())  // 消費者Email，去除前後空白
                .buyerMemo("再來一抽備註")  // 消費備註，去除前後空白
                .cardNo(paymentRequest.getCardNo().trim())  // 信用卡號，去除前後空白
                .expireDate(paymentRequest.getExpireDate().trim())  // 卡片有效日期，去除前後空白
                .cvv(paymentRequest.getCvv().trim())  // 卡片認證碼，去除前後空白
                .transMode("1".trim())  // 交易模式，寫死去除空白
                .installment("0".trim())  // 期數，寫死去除空白
//                .eReturn("1".trim())  // 是否使用Json回傳，寫死去除空白
                .strCheck(STRCHECK.trim())  // 交易驗證密碼，去除前後空白
                .build();


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // 确保使用UTF-8编码

            // 构建请求体
            StringBuilder requestBody = new StringBuilder();
            requestBody.append("Send_Type=").append(URLEncoder.encode(req.getSendType(), "UTF-8"))
                    .append("&Pay_Mode_No=").append(URLEncoder.encode(req.getPayModeNo(), "UTF-8"))
                    .append("&CustomerId=").append(URLEncoder.encode(req.getCustomerId(), "UTF-8"))
                    .append("&Order_No=") // 不需要传值，保持原样
                    .append("&Amount=").append(URLEncoder.encode(req.getAmount().toString(), "UTF-8"))
                    .append("&TransCode=").append(URLEncoder.encode(req.getTransCode(), "UTF-8"))
                    .append("&Buyer_Name=").append(URLEncoder.encode(req.getBuyerName(), "UTF-8"))
                    .append("&Buyer_Telm=").append(URLEncoder.encode(req.getBuyerTelm(), "UTF-8"))
                    .append("&Buyer_Mail=").append(URLEncoder.encode(req.getBuyerMail(), "UTF-8"))
                    .append("&Buyer_Memo=").append(URLEncoder.encode(req.getBuyerMemo(), "UTF-8"))
                    .append("&CardNo=").append(URLEncoder.encode(req.getCardNo(), "UTF-8"))
                    .append("&ExpireDate=").append(URLEncoder.encode(req.getExpireDate().replace("/", "").substring(2) + req.getExpireDate().substring(0, 2), "UTF-8"))
                    .append("&CVV=").append(URLEncoder.encode(req.getCvv(), "UTF-8"))
                    .append("&TransMode=").append(URLEncoder.encode(req.getTransMode(), "UTF-8"))
                    .append("&Installment=").append(URLEncoder.encode(req.getInstallment(), "UTF-8"))
                    .append("&Return_url=") // 不需要传值，保持原样
                    .append("&Callback_Url=") // 不需要传值，保持原样
                    .append("&e_return=")
                    .append("&Str_Check=").append(URLEncoder.encode(req.getStrCheck(), "UTF-8"));

            post.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8)); // 设置请求体为UTF-8编码

            System.out.println(requestBody);

            // 发送请求并接收响应
            HttpResponse response = httpClient.execute(post);
            System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

            // 处理响应体，确保使用UTF-8解码
            String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println("Response JSON: " + jsonResponse);

            Gson gson = new Gson();
            PaymentResponse paymentResponse = gson.fromJson(jsonResponse, PaymentResponse.class);
            System.out.println(paymentResponse);

            return paymentResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
return null;
    }


    public PaymentResponse webATM(PaymentRequest paymentRequest) {
       String url = "https://n.gomypay.asia/ShuntClass.aspx";  //正式
        // String url = "https://n.gomypay.asia/TestShuntClass.aspx";  //測試

        PaymentRequest req = PaymentRequest.builder()
                .sendType("4".trim())  // 傳送型態，去除空白
                .payModeNo("2".trim())  // 付款模式，去除空白
                .customerId(CUSTOMERID.trim())  // 商店代號，去除前後空白
                .amount(paymentRequest.getAmount())  // 交易金額，不需要trim處理
                .buyerName(paymentRequest.getBuyerName().trim())  // 消費者姓名，去除前後空白
                .buyerTelm(paymentRequest.getBuyerTelm().trim())  // 消費者手機，去除前後空白
                .buyerMail(paymentRequest.getBuyerMail().trim())  // 消費者Email，去除前後空白
                .buyerMemo("再來一抽備註")  // 消費備註，去除前後空白
                .callbackUrl("https://api.onemorelottery.tw:8081/payment/paymentCallback".trim())  // 背景對帳網址，去除空白
                .eReturn("1".trim())  // 是否使用Json回傳，去除空白
                .strCheck(STRCHECK.trim())  // 交易驗證密碼，去除前後空白
                .build();
        System.out.println(req);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // 确保使用UTF-8

            // 构建请求体
            StringBuilder requestBody = new StringBuilder();
            requestBody.append("Send_Type=").append(URLEncoder.encode(req.getSendType(), "UTF-8"))
                    .append("&Pay_Mode_No=").append(URLEncoder.encode(req.getPayModeNo(), "UTF-8"))
                    .append("&CustomerId=").append(URLEncoder.encode(req.getCustomerId(), "UTF-8"))
                    .append("&Order_No=") // 不需要传值，保持原样
                    .append("&Amount=").append(URLEncoder.encode(req.getAmount().toString(), "UTF-8"))
                    .append("&Buyer_Name=").append(URLEncoder.encode(req.getBuyerName(), "UTF-8"))
                    .append("&Buyer_Telm=").append(URLEncoder.encode(req.getBuyerTelm(), "UTF-8"))
                    .append("&Buyer_Mail=").append(URLEncoder.encode(req.getBuyerMail(), "UTF-8"))
                    .append("&Buyer_Memo=").append(URLEncoder.encode(req.getBuyerMemo(), "UTF-8"))
                    .append("&Return_url=") // 不需要传值，保持原样
                    .append("&Callback_Url=").append(URLEncoder.encode(req.getCallbackUrl(), "UTF-8"))
                    .append("&e_return=").append(URLEncoder.encode(req.getEReturn(), "UTF-8"))
                    .append("&Str_Check=").append(URLEncoder.encode(req.getStrCheck(), "UTF-8"));

            post.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8)); // 设置请求体为UTF-8编码

            System.out.println(requestBody);
            // 发送请求并接收响应
            HttpResponse response = httpClient.execute(post);
            System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

            // 处理响应体
            String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8); // 确保响应体按UTF-8处理
            System.out.println("Response JSON: " + jsonResponse);

            Gson gson = new Gson();
            PaymentResponse paymentResponse = gson.fromJson(jsonResponse, PaymentResponse.class);

            return paymentResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public PaymentResponse webATM2(PaymentRequest paymentRequest) {
       String url = "https://n.gomypay.asia/ShuntClass.aspx";  //正式
        // String url = "https://n.gomypay.asia/TestShuntClass.aspx";  //測試

        PaymentRequest req = PaymentRequest.builder()
                .sendType("4".trim())  // 傳送型態，去除空白
                .payModeNo("2".trim())  // 付款模式，去除空白
                .customerId(CUSTOMERID.trim())  // 商店代號，去除空白
                .amount(paymentRequest.getAmount())  // 交易金額
                .buyerName(paymentRequest.getBuyerName().trim())  // 消費者姓名，去除空白
                .buyerTelm(paymentRequest.getBuyerTelm().trim())  // 消費者手機，去除空白
                .buyerMail(paymentRequest.getBuyerMail().trim())  // 消費者Email，去除空白
                .buyerMemo("再來一抽備註")  // 消費備註，去除空白
                .callbackUrl("https://api.onemorelottery.tw:8081/payment/paymentCallback2".trim())  // 背景對帳網址，去除空白
                .eReturn("1".trim())  // 是否使用Json回傳，去除空白
                .strCheck(STRCHECK.trim())  // 交易驗證密碼，去除空白
                .build();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // 确保使用UTF-8

            // 构建请求体
            StringBuilder requestBody = new StringBuilder();
            requestBody.append("Send_Type=").append(URLEncoder.encode(req.getSendType(), "UTF-8"))
                    .append("&Pay_Mode_No=").append(URLEncoder.encode(req.getPayModeNo(), "UTF-8"))
                    .append("&CustomerId=").append(URLEncoder.encode(req.getCustomerId(), "UTF-8"))
                    .append("&Order_No=") // 不需要传值，保持原样
                    .append("&Amount=").append(URLEncoder.encode(req.getAmount().toString(), "UTF-8"))
                    .append("&Buyer_Name=").append(URLEncoder.encode(req.getBuyerName(), "UTF-8"))
                    .append("&Buyer_Telm=").append(URLEncoder.encode(req.getBuyerTelm(), "UTF-8"))
                    .append("&Buyer_Mail=").append(URLEncoder.encode(req.getBuyerMail(), "UTF-8"))
                    .append("&Buyer_Memo=").append(URLEncoder.encode(req.getBuyerMemo(), "UTF-8"))
                    .append("&Return_url=") // 不需要传值，保持原样
                    .append("&Callback_Url=").append(URLEncoder.encode(req.getCallbackUrl(), "UTF-8"))
                    .append("&e_return=").append(URLEncoder.encode(req.getEReturn(), "UTF-8"))
                    .append("&Str_Check=").append(URLEncoder.encode(req.getStrCheck(), "UTF-8"));

            post.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8)); // 设置请求体为UTF-8编码

            System.out.println(requestBody);
            // 发送请求并接收响应
            HttpResponse response = httpClient.execute(post);
            System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

            // 处理响应体
            String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8); // 确保响应体按UTF-8处理
            System.out.println("Response JSON: " + jsonResponse);

            Gson gson = new Gson();
            PaymentResponse paymentResponse = gson.fromJson(jsonResponse, PaymentResponse.class);

            return paymentResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public PaymentResponse topOp(PaymentRequest paymentRequest, String payMethod , Long userId) throws Exception {
        PaymentResponse response = null;
        if("2".equals(payMethod)){
            String orderNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            String amountInCents = paymentRequest.getAmount();
            BigDecimal amount = new BigDecimal(amountInCents);
            UserRes user = userRepository.getUserById(userId);
            paymentRequest.setBuyerName(user.getNickname());
            paymentRequest.setBuyerMail(user.getUsername());
            paymentRequest.setBuyerTelm(user.getPhoneNumber());
            response = this.webATM2(paymentRequest);
            userTransactionRepository.insertTransaction2(userId, "DEPOSIT", amount , response.getOrderId());
        }


        return response;
    }

    /**
     * 计算奖励金额
     */
    public int calculateReward(BigDecimal totalAmount) {
        if (totalAmount.compareTo(BigDecimal.valueOf(100000)) >= 0) {
            return 10000;
        } else if (totalAmount.compareTo(BigDecimal.valueOf(50000)) >= 0) {
            return 4000;
        } else if (totalAmount.compareTo(BigDecimal.valueOf(30000)) >= 0) {
            return 2000;
        } else if (totalAmount.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            return 500;
        } else if (totalAmount.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            return 200;
        } else if (totalAmount.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return 30;
        }
        return 0; // 没有达标
    }

    /**
     * 获取用户当月的累积消费金额
     */
    public Award getTotalConsumeAmountForCurrentMonth(Long userId) {
        // 計算本月的起始與結束日期
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        // 獲取該用戶當月的消費總金額
        BigDecimal deposit = userTransactionRepository.getTotalAmountForUserAndMonth(userId, "CONSUME", startOfMonth, endOfMonth);

        // 初始化 Award 物件
        Award award = new Award();
        award.setCumulative(deposit);

        // 累計滿額條件和對應代幣數量
        int[] thresholds = {1000, 5000, 10000, 30000, 50000, 100000};
        int[] tokens = {30, 200, 500, 2000, 4000, 10000};

        // 創建 rewardStatusList 列表
        List<RewardStatus> rewardStatusList = new ArrayList<>();

        // 遍歷閾值和獎勵代幣
        for (int i = 0; i < thresholds.length; i++) {
            BigDecimal threshold = BigDecimal.valueOf(thresholds[i]);
            int tokenAmount = tokens[i];
            boolean achieved = deposit.compareTo(threshold) >= 0;

            // 無論是否達標，都要返回 RewardStatus
            rewardStatusList.add(new RewardStatus(threshold, tokenAmount, achieved));
        }

        // 設置結果到 Award 物件
        award.setRewardStatusList(rewardStatusList);

        // 檢查是否有達標但未領取的獎勵
        for (RewardStatus status : rewardStatusList) {
            if (status.isAchieved()) {
                // 檢查該門檻是否已領取過
                boolean hasReceivedThisReward = userRewardRepository.hasReceivedRewardForThreshold(
                        userId,
                        startOfMonth,
                        endOfMonth,
                        status.getThreshold()
                );

                if (!hasReceivedThisReward) {
                    // 更新用戶銀幣餘額
                    userRepository.updateSliverCoin(userId, BigDecimal.valueOf(status.getSliver()));

                    // 記錄獎勵發放
                    UserReward userReward = new UserReward();
                    userReward.setUserId(userId);
                    userReward.setRewardAmount(BigDecimal.valueOf(status.getSliver()));
                    userReward.setRewardDate(LocalDate.now());
                    userReward.setThresholdAmount(status.getThreshold());
                    userReward.setCreatedAt(LocalDate.now());
                    userRewardRepository.save(userReward);
                }
            }
        }

        return award;
    }


    // Method to create default RewardStatus list when no rewards have been given
    private List<RewardStatus> createDefaultRewardStatusList() {
        int[] thresholds = {1000, 5000, 10000, 30000, 50000, 100000};
        int[] tokens = {30, 200, 500, 2000, 4000, 10000};
        List<RewardStatus> defaultRewardStatusList = new ArrayList<>();

        for (int i = 0; i < thresholds.length; i++) {
            BigDecimal threshold = BigDecimal.valueOf(thresholds[i]);
            int tokenAmount = tokens[i];
            // 添加默認未達標的 RewardStatus
            defaultRewardStatusList.add(new RewardStatus(threshold, tokenAmount, false));
        }

        return defaultRewardStatusList;
    }







    /**
     * 记录储值交易
     */
    public String recordDeposit(Long userId, BigDecimal amount , String orderId) throws MessagingException {
        System.out.println("Send_Type: " + userId);
        System.out.println("Send_Type: " + amount);
        System.out.println("Send_Type: " + orderId);
        int amountInCents = amount.intValue(); // 转为整数分
        userRepository.updateBalance(userId, amountInCents);
        userTransactionRepository.updateByTop(orderId);
        UserRes userById = userRepository.getUserById(userId);

        //訂單成立開立發票並且傳送至email
        ReceiptReq invoiceRequest = new ReceiptReq();
        invoiceRequest.setEmail(userById.getUsername());
        invoiceRequest.setTotalFee(String.valueOf(amount));
        List<ReceiptReq.Item> items = new ArrayList<>();
        ReceiptReq.Item item = new ReceiptReq.Item();
        item.setName("代幣");
        item.setNumber(1);
        item.setMoney(amount.intValue());
        items.add(item);
        invoiceRequest.setItems(items);

        ResponseEntity<ReceiptRes> res = invoiceService.addB2CInvoice(invoiceRequest);
        ReceiptRes receiptRes = res.getBody();
        invoiceService.getInvoicePicture(receiptRes.getCode() , userById.getId());
        return orderId;
    }

    public String recordDeposit3(Long userId, BigDecimal amount) throws MessagingException {
        String orderNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        int amountInCents = amount.intValue(); // 转为整数分
        userTransactionRepository.insertTransaction2(userId, "DEPOSIT", amount, orderNumber);
        return orderNumber;
    }

    /**
     * 记录消费交易
     */
//    public void recordConsume(Long userId, BigDecimal amount) {
//        userTransactionRepository.insertTransaction(userId, "CONSUME", amount);
//    }


    private final OrderRepository orderMapper;
    private final OrderDetailRepository orderDetailMapper;

    @Autowired
    public PaymentService(OrderTempMapper orderTempMapper, OrderDetailTempMapper orderDetailTempMapper,
                        OrderRepository orderMapper, OrderDetailRepository orderDetailMapper) {
        this.orderTempMapper = orderTempMapper;
        this.orderDetailTempMapper = orderDetailTempMapper;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
    }

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private PrizeCartRepository prizeCartRepository;

    @Autowired
    private PrizeCartItemRepository prizeCartItemRepository;

    @Autowired
    private PrizeCartItemService prizeCartItemService;

    @Autowired
    private StoreProductRepository storeProductRepository;

    @Transactional
    public String transferOrderFromTemp(String orderId) throws MessagingException {
        OrderRes order = orderMapper.findOrderByOrderNumber(orderId);
        orderMapper.updateStatus(order.getId());
        UserRes userById = userRepository.getUserById(order.getUserId());
        List<OrderDetailRes> orderDetailsByOrderId = orderDetailRepository.findOrderDetailsByOrderId(order.getId());
        Long cartIdByUserId = cartRepository.getCartIdByUserId(order.getUserId());
        List<CartItem> cartItemList = cartItemRepository.find(cartIdByUserId);
        Long cartIdByUserId1 = prizeCartRepository.getCartIdByUserId(order.getUserId());
        List<PrizeCartItem> prizeCartItemList = prizeCartItemRepository.find(cartIdByUserId1);


        if("1".equals(order.getType())){
            // 獲取所有購物車項的ID並移除
            List<Long> cartItemIds = cartItemList.stream().map(CartItem::getCartItemId).collect(Collectors.toList());

            // 移除購物車項
            cartItemService.removeCartItems(cartItemIds, cartItemList.get(0).getCartId());
        }else if("2".equals(order.getType())){
// 獲取所有購物車項的ID並移除
            List<Long> cartItemIds = prizeCartItemList.stream().map(PrizeCartItem::getPrizeCartItemId).collect(Collectors.toList());

            // 移除購物車項
            prizeCartItemService.removeCartItems(cartItemIds, prizeCartItemList.get(0).getCartId());
        }




        //訂單成立開立發票並且傳送至email
        ReceiptReq invoiceRequest = new ReceiptReq();
        if(order.getVehicle() != null){
            invoiceRequest.setOrderCode(order.getVehicle());
        }
        invoiceRequest.setEmail(userById.getUsername());
        if(order.getState() != null){
            invoiceRequest.setState(1);
            invoiceRequest.setDonationCode(order.getDonationCode());
        }else{
            invoiceRequest.setState(0);
        }
        BigDecimal amountToSend = order.getTotalAmount();
        invoiceRequest.setTotalFee(String.valueOf(amountToSend));
        List<ReceiptReq.Item> items = new ArrayList<>();
        for(OrderDetailRes cartItem : orderDetailsByOrderId){
            ReceiptReq.Item item = new ReceiptReq.Item();
            ProductDetailRes byId = productDetailRepository.getProductDetailById(cartItem.getProductDetailRes().getProductDetailId());
            StoreProductRes resById = storeProductRepository.findResById(cartItem.getStoreProduct().getStoreProductId());
            if(byId != null){
                if(byId.getProductName() != null){
                    item.setName(byId.getProductName());
                }else{
                    item.setName("商品");
                }
                item.setNumber(cartItem.getQuantity());
                if(cartItem.getUnitPrice() != null){
                    item.setMoney(cartItem.getUnitPrice().intValue());
                }else{
                    item.setMoney(cartItem.getTotalPrice().intValue());
                }
                items.add(item);
            }else if(resById != null){
                if(resById.getProductName() != null){
                    item.setName(resById.getProductName());
                }else{
                    item.setName("商品");
                }
                item.setNumber(cartItem.getQuantity());
                if(cartItem.getUnitPrice() != null){
                    item.setMoney(cartItem.getUnitPrice().intValue());
                }else{
                    item.setMoney(cartItem.getTotalPrice().intValue());
                }
                items.add(item);
            }

        }
        invoiceRequest.setItems(items);

        ResponseEntity<ReceiptRes> res = invoiceService.addB2CInvoice(invoiceRequest);
        ReceiptRes receiptRes = res.getBody();
        invoiceService.getInvoicePicture(receiptRes.getCode() , userById.getId());

        return order.getType();

    }

    private Order convertToOrder(OrderTemp orderTemp) {
        return Order.builder()
                .orderNumber(orderTemp.getOrderNumber())
                .userId(orderTemp.getUserId())
                .totalAmount(orderTemp.getTotalAmount())
                .shippingCost(orderTemp.getShippingCost())
                .isFreeShipping(orderTemp.getIsFreeShipping())
                .bonusPointsEarned(orderTemp.getBonusPointsEarned())
                .bonusPointsUsed(orderTemp.getBonusPointsUsed())
                .createdAt(orderTemp.getCreatedAt())
                .updatedAt(orderTemp.getUpdatedAt())
                .paidAt(orderTemp.getPaidAt())
                .resultStatus(orderTemp.getResultStatus())
                .paymentMethod(orderTemp.getPaymentMethod())
                .shippingMethod(orderTemp.getShippingMethod())
                .shippingName(orderTemp.getShippingName())
                .shippingZipCode(orderTemp.getShippingZipCode())
                .shippingCity(orderTemp.getShippingCity())
                .shippingArea(orderTemp.getShippingArea())
                .shippingAddress(orderTemp.getShippingAddress())
                .billingZipCode(orderTemp.getBillingZipCode())
                .billingName(orderTemp.getBillingName())
                .billingCity(orderTemp.getBillingCity())
                .billingArea(orderTemp.getBillingArea())
                .billingAddress(orderTemp.getBillingAddress())
                .invoice(orderTemp.getInvoice())
                .trackingNumber(orderTemp.getTrackingNumber())
                .shippingPhone(orderTemp.getShippingPhone())
                .shopId(orderTemp.getShopId())
                .OPMode(orderTemp.getOPMode())
                .build();
    }

    private List<OrderDetail> convertToOrderDetails(Long orderId, List<OrderDetailTemp> orderDetailTemps) {
        return orderDetailTemps.stream().map(detailTemp -> OrderDetail.builder()
                .orderId(orderId)
                .productDetailId(detailTemp.getProductDetailId())
                .storeProductId(detailTemp.getStoreProductId())
                .quantity(detailTemp.getQuantity())
                .totalPrice(detailTemp.getTotalPrice())
                .bonusPointsEarned(detailTemp.getBonusPointsEarned())
                .build()
        ).collect(Collectors.toList());
    }

    public Boolean recordDeposit2(CreditDto creditDto) throws MessagingException {
        String status = userTransactionRepository.findByOrderNumber(creditDto.getOrderNumber());
        UserTransaction userTransaction = userTransactionRepository.findByOrderNumber2(creditDto.getOrderNumber());
        if(status == null){
            return null;
        }


        if ("IS_PAY".equals(status)) {
            return false;
        } else {
            BigDecimal amountDecimal = userTransaction.getAmount();
            int amount = amountDecimal.intValue();
            userRepository.updateBalance(userTransaction.getUserId(), amount);
            userTransactionRepository.updateStatus(creditDto);
            //訂單成立開立發票並且傳送至email
            UserRes userById = userRepository.getUserById(userTransaction.getUserId());
            ReceiptReq invoiceRequest = new ReceiptReq();
            invoiceRequest.setEmail(userById.getUsername());
            invoiceRequest.setTotalFee(String.valueOf(amount));
            List<ReceiptReq.Item> items = new ArrayList<>();
            ReceiptReq.Item item = new ReceiptReq.Item();
            item.setName("代幣");
            item.setNumber(1);
            item.setMoney(amount);
            items.add(item);
            invoiceRequest.setItems(items);

            ResponseEntity<ReceiptRes> res = invoiceService.addB2CInvoice(invoiceRequest);
            ReceiptRes receiptRes = res.getBody();
            invoiceService.getInvoicePicture(receiptRes.getCode() , userById.getId());
            return true;
        }
    }


    public OrderDetail mapCartItemToOrderDetail(CartItem cartItem, Long orderId ,  String billNumber ) {
        BigDecimal totalPrice = cartItem.getUnitPrice().multiply(new BigDecimal(cartItem.getQuantity()));

        return OrderDetail.builder().orderId(orderId).storeProductId(cartItem.getStoreProductId())
                .quantity(cartItem.getQuantity()).unitPrice(cartItem.getUnitPrice()).totalPrice(totalPrice) // 新增
                .billNumber(billNumber)																						// totalPrice
                .build();
    }
}
