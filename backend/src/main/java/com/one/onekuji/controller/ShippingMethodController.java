package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.request.ShippingMethodReq;
import com.one.onekuji.response.ShippingMethodRes;
import com.one.onekuji.service.ShippingMethodService;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
public class ShippingMethodController {

    @Autowired
    private ShippingMethodService shippingMethodService;

    @GetMapping("/method")
    public ResponseEntity<ApiResponse<List<ShippingMethodRes>>> getShippingMethods() {
        List<ShippingMethodRes> shippingMethods = shippingMethodService.getAllShippingMethods();
        ApiResponse<List<ShippingMethodRes>> response = ResponseUtils.success(200, null, shippingMethods);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/method/{id}")
    public ResponseEntity<ApiResponse<ShippingMethodRes>> getShippingMethodById(@PathVariable Long id) {
        ShippingMethodRes shippingMethod = shippingMethodService.getShippingMethodById(id);
        if (shippingMethod == null) {
            ApiResponse<ShippingMethodRes> response = ResponseUtils.failure(404, "找不到配送方式", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<ShippingMethodRes> response = ResponseUtils.success(200, null, shippingMethod);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/method")
    public ResponseEntity<ApiResponse<ShippingMethodRes>> createShippingMethod(@RequestBody ShippingMethodReq shippingMethodReq) {
        shippingMethodService.createShippingMethod(shippingMethodReq);
        ApiResponse<ShippingMethodRes> response = ResponseUtils.success(201, "創建成功", null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/method/{id}")
    public ResponseEntity<ApiResponse<ShippingMethodRes>> updateShippingMethod(
            @PathVariable Long id, @RequestBody ShippingMethodReq shippingMethodReq) {
        try {
            shippingMethodService.updateShippingMethod(id, shippingMethodReq);
            ApiResponse<ShippingMethodRes> response = ResponseUtils.success(200, "更新成功", null);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @DeleteMapping("/method/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShippingMethod(@PathVariable Long id) {
        shippingMethodService.deleteShippingMethod(id);
        ApiResponse<Void> response = ResponseUtils.success(200, "刪除成功", null);
        return ResponseEntity.ok(response);
    }
}
