package com.one.controller;

import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.service.CartService;
import com.one.frontend.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/getCart")
    public ResponseEntity<?> getCart(){
        var userDetails = SecurityUtils.getCurrentUserPrinciple();
        var userId = userDetails.getId();
        var cartItems = cartService.getCart(userId);
        var res = ResponseUtils.success(200, null, cartItems);
        return ResponseEntity.ok(res);
    }

}
