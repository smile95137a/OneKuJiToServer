package com.one.controller;

import com.one.config.security.SecurityUtils;
import com.one.service.PrizeCartService;
import com.one.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/prizeCart")
public class PrizeCartController {

    @Autowired
    private PrizeCartService prizeCartService;

    @GetMapping("/getCart")
    public ResponseEntity<?> getCart(){
        var userDetails = SecurityUtils.getCurrentUserPrinciple();
        var userId = userDetails.getId();
        var cartItems = prizeCartService.getCart(userId);
        var res = ResponseUtils.success(200, null, cartItems);
        return ResponseEntity.ok(res);
    }
}
