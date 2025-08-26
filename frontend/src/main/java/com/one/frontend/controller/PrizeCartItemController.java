package com.one.frontend.controller;

import com.one.frontend.config.security.CustomUserDetails;
import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.service.PrizeCartItemService;
import com.one.frontend.service.PrizeCartService;
import com.one.frontend.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prize_cart_item")
public class PrizeCartItemController {

    @Autowired
    private PrizeCartItemService prizeCartItemService;

    @Autowired
    private PrizeCartService prizeCartService;

    @DeleteMapping("/remove/{prizeCartItemId}")
    public ResponseEntity<ApiResponse<?>> removeCartItem(@PathVariable Long prizeCartItemId) {
        try {
        CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
        var userId = userDetails.getId();

        Long cartId = prizeCartService.getCartIdByUserId(userId);

        if (cartId == null) {
            var response = ResponseUtils.failure(999, "無法找到賞品盒，請稍後重試", false);
            return ResponseEntity.ok(response);
        }

        boolean result = prizeCartItemService.removeCartItem(userId , cartId , prizeCartItemId);
        if (!result) {
            var response = ResponseUtils.failure(400, "賞品退還失敗", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        var response = ResponseUtils.success(200, "商品成功移除", true);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        var response = ResponseUtils.failure(500, "商品移除失敗", false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    }



}
