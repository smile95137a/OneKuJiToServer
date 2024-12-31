package com.one.controller;

import com.one.config.security.SecurityUtils;
import com.one.dto.CreditDto;
import com.one.model.ApiResponse;
import com.one.model.Award;
import com.one.model.PaymentRequest;
import com.one.repository.OrderRepository;
import com.one.repository.PaymentResponseMapper;
import com.one.repository.UserRepository;
import com.one.response.PaymentResponse;
import com.one.service.PaymentService;
import com.one.util.ResponseUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentResponseMapper paymentResponseMapper;
    @PostMapping("/creditCart")
    public PaymentResponse creditCart(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.creditCard(paymentRequest);
    }

    @PostMapping("/webATM") //虛擬帳戶
    public PaymentResponse webATM(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.webATM(paymentRequest);
    }

    @PostMapping("/paymentCallback")
    public ResponseEntity<String> paymentCallback(
            @RequestParam String Send_Type,
            @RequestParam String result,
            @RequestParam String ret_msg,
            @RequestParam String OrderID,
            @RequestParam String e_money,
            @RequestParam String PayAmount,
            @RequestParam String e_date,
            @RequestParam String e_time,
            @RequestParam String e_orderno,
            @RequestParam String e_payaccount,
            @RequestParam String e_PayInfo,
            @RequestParam String str_check
    ) throws MessagingException {
        // 打印接收到的参数
        System.out.println("Send_Type: " + Send_Type);
        System.out.println("Result: " + result);
        System.out.println("Return Message: " + ret_msg);
        System.out.println("Order ID: " + OrderID);
        System.out.println("e_money: " + e_money);
        System.out.println("Pay Amount: " + PayAmount);
        System.out.println("e_date: " + e_date);
        System.out.println("e_time: " + e_time);
        System.out.println("e_orderno: " + e_orderno);
        System.out.println("e_payaccount: " + e_payaccount);
        System.out.println("e_PayInfo: " + e_PayInfo);
        System.out.println("str_check: " + str_check);
        try {
            if("1".equals(result)){
                paymentService.transferOrderFromTemp(e_orderno);
            } else {
                paymentService.rePrizeCart(e_orderno);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok("Received payment callback successfully");
    }

    @PostMapping("/creditMP")
    public ResponseEntity<ApiResponse<?>> creditpaymentCallback(@RequestBody CreditDto creditDto) {
        try {
            if(paymentService.checkStatus(creditDto.getOrderNumber())){
                String s = paymentService.transferOrderFromTemp(creditDto.getOrderNumber());
                ApiResponse<Object> sc = ResponseUtils.success(200, null, s);
                return ResponseEntity.ok(sc);
            }else{
                ApiResponse<Object> sc = ResponseUtils.failure(999, "此訂單已繳費，請確認訂單狀態是否改為準備發貨", null);
                return ResponseEntity.ok(sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> error = ResponseUtils.failure(500, "系統錯誤，請稍後再試", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/paymentCallbackMP")
    public ResponseEntity<ApiResponse<?>> paymentCallbackMP( @RequestParam String Send_Type,
                                                             @RequestParam String result,
                                                             @RequestParam String ret_msg,
                                                             @RequestParam String OrderID,
                                                             @RequestParam String e_money,
                                                             @RequestParam String PayAmount,
                                                             @RequestParam String e_date,
                                                             @RequestParam String e_time,
                                                             @RequestParam String e_orderno,
                                                             @RequestParam String e_payaccount,
                                                             @RequestParam String e_PayInfo,
                                                             @RequestParam String str_check) {
        try {
            if(paymentService.checkStatus(e_orderno)){
                String s = paymentService.transferOrderFromTemp(e_orderno);
                ApiResponse<Object> sc = ResponseUtils.success(200, null, s);
                return ResponseEntity.ok(sc);
            }else{
                ApiResponse<Object> sc = ResponseUtils.failure(999, "此訂單已繳費，請確認訂單狀態是否改為準備發貨", null);
                return ResponseEntity.ok(sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> error = ResponseUtils.failure(500, "系統錯誤，請稍後再試", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @PostMapping("/paymentCallback2")
    public ResponseEntity<String> paymentCallback2(
            @RequestParam String Send_Type,
            @RequestParam String result,
            @RequestParam String ret_msg,
            @RequestParam String OrderID,
            @RequestParam String e_money,
            @RequestParam String PayAmount,
            @RequestParam String e_date,
            @RequestParam String e_time,
            @RequestParam String e_orderno,
            @RequestParam String e_payaccount,
            @RequestParam String e_PayInfo,
            @RequestParam String str_check
    ) throws MessagingException {
        // 打印接收到的参数
        System.out.println("Send_Type: " + Send_Type);
        System.out.println("Result: " + result);
        System.out.println("Return Message: " + ret_msg);
        System.out.println("Order ID: " + OrderID);
        System.out.println("e_money: " + e_money);
        System.out.println("Pay Amount: " + PayAmount);
        System.out.println("e_date: " + e_date);
        System.out.println("e_time: " + e_time);
        System.out.println("e_orderno: " + e_orderno);
        System.out.println("e_payaccount: " + e_payaccount);
        System.out.println("e_PayInfo: " + e_PayInfo);
        System.out.println("str_check: " + str_check);
        if("1".equals(result)){
            // 记录储值交易
            PaymentResponse byId = paymentResponseMapper.findById(e_orderno);
            paymentService.recordDeposit(byId.getUserId(), new BigDecimal(PayAmount) , e_orderno);
            ApiResponse<Void> response1 = ResponseUtils.success(200, "成功", null);

            return ResponseEntity.ok("Received payment callback successfully");
        }

        return ResponseEntity.ok("Received payment callback successfully");
    }


    @PostMapping("/topOp") //儲值
    public ResponseEntity<ApiResponse<?>> topOp(@RequestBody PaymentRequest paymentRequest) throws Exception {
        var userDetails = SecurityUtils.getCurrentUserPrinciple();
        var userId = userDetails.getId();
        try{
            if("2".equals(paymentRequest.getPaymentMethod()) && Integer.parseInt(paymentRequest.getAmount()) < 20000){
                PaymentResponse response = paymentService.topOp(paymentRequest , paymentRequest.getPaymentMethod() , userId);
                ApiResponse<Object> response1 = ResponseUtils.success(200, null, response);
                return ResponseEntity.ok(response1);
            }else if("1".equals(paymentRequest.getPaymentMethod())){
                int amount = Integer.parseInt(paymentRequest.getAmount());
                String s = paymentService.recordDeposit3(userId, BigDecimal.valueOf(amount));
                ApiResponse<Object> success = ResponseUtils.success(200, "信用卡訂單編號", s);
                return ResponseEntity.ok(success);
            }else{
                ApiResponse<Object> response1 = ResponseUtils.failure(200, "轉帳單筆不得超過兩萬", new ArrayList<>());
                return ResponseEntity.ok(response1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/creditTopOp")
    public ResponseEntity<ApiResponse<?>> creditTopOp(@RequestBody CreditDto creditDto) {
        var userDetails = SecurityUtils.getCurrentUserPrinciple();
        var userId = userDetails.getId();
        try {
            if("1".equals(creditDto.getCreditResult())){
                Boolean status = paymentService.recordDeposit2(creditDto);
                if(status == null){
                    ApiResponse<Object> failure = ResponseUtils.failure(400, "無付款資訊", null);
                    return ResponseEntity.ok(failure);
                }
                if (status) {
                    ApiResponse<Object> success = ResponseUtils.success(200, "付款成功，信用卡狀態已為已付款", null);
                    return ResponseEntity.ok(success);
                } else {
                    ApiResponse<Object> failure = ResponseUtils.failure(400, "已付款訂單編號，不得重複刷新", null);
                    return ResponseEntity.ok(failure);
                }
            }else{
                ApiResponse<Object> failure = ResponseUtils.failure(400, "儲值失敗", null);
                    return ResponseEntity.ok(failure);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<Object> error = ResponseUtils.failure(500, "系統錯誤，請稍後再試", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /**
     * 领取消费奖励
     */
    @GetMapping("/claim")
    public ResponseEntity<?> claimReward() {
        var userDetails = SecurityUtils.getCurrentUserPrinciple();
        var userId = userDetails.getId();
        BigDecimal totalConsumeAmount = paymentService.getTotalConsumeAmountForCurrentMonth(userId).getCumulative();
        int rewardAmount = paymentService.calculateReward(totalConsumeAmount);

        if (rewardAmount > 0) {
            // 更新用户银币
            userRepository.updateSliverCoin(userId, BigDecimal.valueOf(rewardAmount));
            return ResponseEntity.ok("Successfully claimed " + rewardAmount + " silver coins!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No rewards available for this user.");
        }
    }

    @GetMapping("/getTotal")
    public ResponseEntity<ApiResponse<Award>> getTotal() {
        var userDetails = SecurityUtils.getCurrentUserPrinciple();
        var userId = userDetails.getId();

        // 调用 service 获取该用户当前月的消费总额和奖励信息
        Award totalConsumeAmount = paymentService.getTotalConsumeAmountForCurrentMonth(userId);

        // 创建一个成功的 ApiResponse 对象，包含消费总额数据
        ApiResponse<Award> resultTotal = ResponseUtils.success(200, null, totalConsumeAmount);

        // 返回响应实体，包含 ApiResponse 对象
        return ResponseEntity.ok(resultTotal);
    }



}
