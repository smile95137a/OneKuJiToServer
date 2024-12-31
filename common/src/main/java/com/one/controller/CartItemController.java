package com.one.controller;

import com.one.frontend.config.security.CustomUserDetails;
import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.request.CartItemReq;
import com.one.frontend.service.CartItemService;
import com.one.frontend.service.CartService;
import com.one.frontend.service.StoreProductService;
import com.one.frontend.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cartItem")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addCartItem(@RequestBody CartItemReq cartItem) {
        try {
            // 取得使用者ID
            CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
            var userId = userDetails.getId();

            Long cartId = cartService.getCartIdByUserId(userId);

            if (cartId == null) {
                var response = ResponseUtils.failure(999, "無法找到購物車，請稍後重試", false);
                return ResponseEntity.ok(response);
            }

            var result = cartItemService.addCartItem(cartId, cartItem);
            var response = ResponseUtils.success(201, "商品成功加到購物車", result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
        	e.printStackTrace();
            var response = ResponseUtils.failure(500, "商品新增失敗", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/checkQua")
    public ResponseEntity<ApiResponse<?>> checkQu(@RequestBody CartItemReq quantity) {
        try {
            // 取得使用者ID
            CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
            var userId = userDetails.getId();

            Boolean result = cartItemService.checkQu(quantity , userId);
            var response = ResponseUtils.success(200, "數量正確", result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            var response = ResponseUtils.failure(200, "已達商品數量上限", false);
            return ResponseEntity.ok(response);
        }
    }


    
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<ApiResponse<?>> removeCartItem(@PathVariable Long cartItemId) {
        try {
            // 取得使用者ID
            CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
            var userUid = userDetails.getId();

            Long cartId = cartService.getCartIdByUserId(userUid);

            if (cartId == null) {
                var response = ResponseUtils.failure(999, "無法找到購物車，請稍後重試", false);
                return ResponseEntity.ok(response);
            } 

            boolean result = cartItemService.removeCartItem(cartId, cartItemId);
            if (!result) {
                var response = ResponseUtils.failure(400, "商品移除失敗", false);
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
