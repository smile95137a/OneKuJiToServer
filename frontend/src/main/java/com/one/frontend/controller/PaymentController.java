package com.one.frontend.controller;

import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.Award;
import com.one.frontend.model.PaymentRequest;
import com.one.frontend.repository.UserRepository;
import com.one.frontend.response.PaymentResponse;
import com.one.frontend.service.PaymentService;
import com.one.frontend.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/creditCart")
    public PaymentResponse creditCart(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.creditCard(paymentRequest);
    }

    @PostMapping("/webATM") //虛擬帳戶
    public PaymentResponse webATM(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.webATM(paymentRequest);
    }


    @PostMapping("/topOp") //儲值
    public ResponseEntity<ApiResponse<?>> topOp(@RequestBody PaymentRequest paymentRequest) throws Exception {
        var userDetails = SecurityUtils.getCurrentUserPrinciple();
        var userId = userDetails.getId();
        PaymentResponse response = paymentService.topOp(paymentRequest , paymentRequest.getPaymentMethod() , userId);
        int amount = Integer.parseInt(response.getAmount());
        String result = response.getResult();
        if ("1".equals(result)) {
            // 记录储值交易
            paymentService.recordDeposit(userId, BigDecimal.valueOf(amount));
            ApiResponse<Void> response1 = ResponseUtils.success(200, "成功", null);
            return ResponseEntity.ok(response1);
        }
        ApiResponse<Boolean> response1 = ResponseUtils.failure(200, "失敗", null);

        return ResponseEntity.ok(response1);
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
