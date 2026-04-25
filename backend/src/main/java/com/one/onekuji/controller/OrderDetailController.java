package com.one.onekuji.controller;

import com.one.onekuji.model.OrderDetail;
import com.one.onekuji.response.OrderDetailRes;
import com.one.onekuji.service.OrderDetailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orderDetails")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Operation(summary = "根據訂單ID取得所有明細", description = "根據 order_id 查詢該訂單所有明細項目")
    @GetMapping("/{orderId}")
    public ResponseEntity<List<OrderDetailRes>> getOrderDetailsByOrderId(@PathVariable Long orderId) {
        List<OrderDetailRes> details = orderDetailService.findByOrderId(orderId);
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

}
