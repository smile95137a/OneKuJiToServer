package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.service.RedemptionCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redemption")
public class RedemptionCodeController {

    @Autowired
    private RedemptionCodeService redemptionCodeService;

    // 生成新的兌換碼
    @PostMapping("/generate/{productId}")
    public String generateRedemptionCode(@PathVariable Long productId) {
        return redemptionCodeService.generateRedemptionCode(productId);
    }

    // 兌換兌換碼
    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<?>> redeemCode() {
        ApiResponse<?> response = new ApiResponse<>(200, "找不到banner", true, redemptionCodeService.redeemCode());
        return ResponseEntity.ok(response);
    }
}
