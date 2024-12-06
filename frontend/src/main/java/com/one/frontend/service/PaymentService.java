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
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

//       String url = "https://n.gomypay.asia/ShuntClass.aspx";  //正式
         String url = "https://n.gomypay.asia/TestShuntClass.aspx";  //測試

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
//         String url = "https://n.gomypay.asia/TestShuntClass.aspx";  //測試

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
//         String url = "https://n.gomypay.asia/TestShuntClass.aspx";  //測試

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
            paymentRequest.setBuyerMail(user.getEmail());
            paymentRequest.setBuyerTelm(user.getPhoneNumber());
            LocalDateTime localDateTime = LocalDateTime.now();
            userTransactionRepository.insertTransaction2(userId, "DEPOSIT", amount , orderNumber , localDateTime);
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
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();  // 2024-11-01 00:00:00
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);  // 2024-11-30 23:59:59.999999999

        System.out.println(startOfMonth);
        System.out.println(endOfMonth);
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
        invoiceRequest.setEmail(userById.getEmail());
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
        LocalDateTime localDateTime = LocalDateTime.now();
        userTransactionRepository.insertTransaction2(userId, "DEPOSIT", amount, orderNumber , localDateTime);
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
            if(!cartItemIds.isEmpty()){
                cartItemList.forEach(cartItem -> {
                    // 查詢目前的商品數據
                    StoreProduct storeProduct = storeProductRepository.findById(cartItem.getStoreProductId());

                    // 更新庫存和已售數量
                    int newStockQuantity = storeProduct.getStockQuantity() - cartItem.getQuantity();
                    int newSoldQuantity = (storeProduct.getSoldQuantity() == null ? 0 : storeProduct.getSoldQuantity()) + cartItem.getQuantity();

                    // 判斷是否需要更新為 SOLD_OUT 狀態
                    String newStatus = (newStockQuantity <= 0) ? "SOLD_OUT" : storeProduct.getStatus();

                    // 更新商品信息
                    storeProductRepository.updateStoreProduct(
                            storeProduct.getStoreProductId(),
                            Math.max(newStockQuantity, 0), // 確保庫存不為負
                            newSoldQuantity,
                            newStatus
                    );
                });
                // 移除購物車項
                cartItemService.removeCartItems(cartItemIds, cartItemList.get(0).getCartId());
            }



        }else if("2".equals(order.getType())){
// 獲取所有購物車項的ID並移除
            List<Long> cartItemIds = prizeCartItemList.stream().map(PrizeCartItem::getPrizeCartItemId).collect(Collectors.toList());
            if(!cartItemIds.isEmpty()){
                prizeCartItemService.removeCartItems(cartItemIds, prizeCartItemList.get(0).getCartId());
            }
        }




        // 生成发票请求对象
        ReceiptReq invoiceRequest = new ReceiptReq();

// 设置订单号，如果有车辆信息，则使用车辆作为订单号，否则使用订单ID
        if (order.getVehicle() != null) {
            invoiceRequest.setOrderCode(order.getVehicle());
        } else {
            invoiceRequest.setOrderCode(null);  // 使用订单ID作为备用
        }

// 设置电子邮件
        invoiceRequest.setEmail(userById.getEmail());

// 设置发票状态和捐赠信息
        if (order.getState() != null) {
            invoiceRequest.setState(1);  // 发票捐赠
            invoiceRequest.setDonationCode(order.getDonationCode());  // 设置捐赠码
        } else {
            invoiceRequest.setState(0);  // 非捐赠
            invoiceRequest.setDonationCode(null);
        }

// 确保金额格式正确，并将 BigDecimal 转换为字符串
        String amountToSend = order.getTotalAmount() != null
                ? order.getTotalAmount().setScale(0, RoundingMode.DOWN).toPlainString()  // 强制去掉小数部分
                : "1";
        invoiceRequest.setTotalFee(amountToSend);


// 设置时间戳（当前时间）和其他缺失字段
        invoiceRequest.setTimeStamp(String.valueOf(System.currentTimeMillis()));  // 当前时间戳
        invoiceRequest.setCustomerName(null);  // 可以根据需要设置客户名
        if(order.getVehicle() != null){
            invoiceRequest.setPhone(order.getVehicle());
        }
        invoiceRequest.setDatetime("2024-09-27 12:34:56");  // 设定日期时间
        invoiceRequest.setTaxType(null);  // 设置税种，如果没有可以为 null
        invoiceRequest.setCompanyCode(null);  // 如果有公司代码，设置
        invoiceRequest.setFreeAmount(null);  // 如果有免费金额，设置
        invoiceRequest.setZeroAmount(null);  // 如果有零金额，设置
        invoiceRequest.setSales(null);  // 销售人员设置，如果有的话
        invoiceRequest.setContent(null);  // 可能是发票的详细内容，如果有可以填充
        invoiceRequest.setAmount(null);

// 创建并设置商品项列表
        List<ReceiptReq.Item> items = new ArrayList<>();
        for (OrderDetailRes cartItem : orderDetailsByOrderId) {
            ReceiptReq.Item item = new ReceiptReq.Item();

            // 获取产品详细信息和商店产品信息
            ProductDetailRes productDetail = productDetailRepository.getProductDetailById(cartItem.getProductDetailRes().getProductDetailId());
            StoreProductRes storeProduct = storeProductRepository.findResById(cartItem.getStoreProduct().getStoreProductId());

            if (productDetail != null) {
                // 使用产品名称，如果没有则使用“商品”
                item.setName(productDetail.getProductName() != null ? productDetail.getProductName() : "商品");
                item.setNumber(cartItem.getQuantity());
                item.setMoney(cartItem.getUnitPrice() != null ? cartItem.getUnitPrice().intValue() : cartItem.getTotalPrice().intValue());
                item.setTaxType(null);  // 如果有税种信息可以填充
                item.setRemark(null);   // 如果有备注信息可以填充
                items.add(item);
            } else if (storeProduct != null) {
                // 如果没有找到 ProductDetail，则使用 StoreProduct 信息
                item.setName(storeProduct.getProductName() != null ? storeProduct.getProductName() : "商品");
                item.setNumber(cartItem.getQuantity());
                item.setMoney(cartItem.getUnitPrice() != null ? cartItem.getUnitPrice().intValue() : cartItem.getTotalPrice().intValue());
                item.setTaxType(null);
                item.setRemark(null);
                items.add(item);
            } else {
                // 如果都没有找到相关商品信息，可以记录日志或处理异常
                item.setName("商品");
                item.setNumber(cartItem.getQuantity());
                item.setMoney(0);  // 默认金额为 0
                item.setTaxType(null);
                item.setRemark(null);
                items.add(item);
            }
        }

// 设置商品项列表
        invoiceRequest.setItems(items);
        System.out.println(invoiceRequest);
// 调用发票服务生成发票并返回结果
        ResponseEntity<ReceiptRes> res = invoiceService.addB2CInvoice(invoiceRequest);
        ReceiptRes receiptRes = res.getBody();  // 获取发票响应结果
        System.out.println(receiptRes);
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
            UserRes userById2 = userRepository.getUserById(userTransaction.getUserId());
            ReceiptReq invoiceRequest = new ReceiptReq();
            invoiceRequest.setEmail(userById2.getEmail());
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
            invoiceService.getInvoicePicture(receiptRes.getCode() , userTransaction.getUserId());
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

    public void rePrizeCart(String orderID) {
        OrderRes orderByOrderNumber = orderMapper.findOrderByOrderNumber(orderID);
        orderMapper.updateStatusByFail(orderByOrderNumber.getId());
        List<OrderDetailRes> orderDetailsByOrderId = orderDetailMapper.findOrderDetailsByOrderId(orderByOrderNumber.getId());
        Long cartIdByUserId = cartRepository.getCartIdByUserId(orderByOrderNumber.getUserId());
        Long cartIdByUserId1 = prizeCartRepository.getCartIdByUserId(orderByOrderNumber.getUserId());
        if("1".equals(orderByOrderNumber.getType())){
            for(OrderDetailRes detailRes:orderDetailsByOrderId){
                CartItem cartItem = new CartItem();
                cartItem.setCartId(cartIdByUserId);
                cartItem.setStoreProductId(detailRes.getStoreProduct().getStoreProductId());
                cartItem.setQuantity(detailRes.getQuantity());
                cartItem.setUnitPrice(detailRes.getUnitPrice());
                cartItem.setTotalPrice(BigDecimal.valueOf(detailRes.getTotalPrice()));
                cartItem.setSize(detailRes.getStoreProduct().getSize());
                cartItem.setIsSelected(true);
                cartItemRepository.addCartItem(cartItem);
            }
        }else if("2".equals(orderByOrderNumber.getType())){
            List<PrizeCartItem> prizeCartItemList = new ArrayList<>();
            for(OrderDetailRes detailRes:orderDetailsByOrderId) {
                PrizeCartItem prizeCartItem = new PrizeCartItem();
                prizeCartItem.setCartId(cartIdByUserId1);
                prizeCartItem.setProductDetailId(detailRes.getProductDetailRes().getProductDetailId());
                prizeCartItem.setQuantity(detailRes.getQuantity());
                prizeCartItem.setSliverPrice(detailRes.getProductDetailRes().getSliverPrice());
                prizeCartItem.setIsSelected(true);
                prizeCartItem.setSize(detailRes.getProductDetailRes().getSize());
                prizeCartItemList.add(prizeCartItem);
            }
            prizeCartItemRepository.insertBatch(prizeCartItemList);
        }

    }
}
