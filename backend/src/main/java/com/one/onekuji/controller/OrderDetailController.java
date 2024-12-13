package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.OrderDetail;
import com.one.onekuji.service.OrderDetailService;
import com.one.onekuji.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orderDetails")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Operation(summary = "根据ID获取订单详情", description = "根据ID获取订单详情记录")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetail>> getOrderDetailById(@PathVariable Long id) {
        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
        if (orderDetail != null) {
            ApiResponse<OrderDetail> response = ResponseUtils.success(200, null, orderDetail);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<OrderDetail> response = ResponseUtils.success(200, null, null);
            return ResponseEntity.ok(response);
        }
    }

}
