package com.one.controller;

import com.one.model.ApiResponse;
import com.one.response.ShippingMethodRes;
import com.one.service.ShippingMethodService;
import com.one.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/shipping")
public class ShippingMethodController {

    @Autowired
    private ShippingMethodService shippingMethodService;

    @GetMapping("/method")
    public ResponseEntity<ApiResponse<List<ShippingMethodRes>>> getShippingMethod(
            @RequestParam("size") BigDecimal size) {
        List<ShippingMethodRes> shippingMethodRes = shippingMethodService.getShippingMethod(size);
        if (shippingMethodRes == null) {
            ApiResponse<List<ShippingMethodRes>> response = ResponseUtils.failure(404, "無適用方式", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<List<ShippingMethodRes>> response = ResponseUtils.success(200, null, shippingMethodRes);
        return ResponseEntity.ok(response);
    }


}
