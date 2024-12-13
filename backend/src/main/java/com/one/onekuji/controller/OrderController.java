package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.Order;
import com.one.onekuji.request.OrderQueryReq;
import com.one.onekuji.request.OrderStatusUpdateRequest;
import com.one.onekuji.response.OrderRes;
import com.one.onekuji.service.OrderService;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/query")
    public ResponseEntity<ApiResponse<List<OrderRes>>> getAllOrders() {
        try {
            OrderQueryReq req = new OrderQueryReq();
            List<OrderRes> orders = orderService.getAllOrders(req);
            ApiResponse<List<OrderRes>> response = ResponseUtils.success(200, null, orders);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            ApiResponse<Order> response = ResponseUtils.success(200, null, order);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Order> response = ResponseUtils.failure(200, null, null);
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateOrder(@PathVariable Long id, @RequestBody OrderStatusUpdateRequest request) {
        try {
            String resultStatus = request.getResultStatus(); // 从 JSON 中获取 resultStatus
            String s = orderService.updateOrder(id, resultStatus);
            ApiResponse<String> response = ResponseUtils.success(200, null, s);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> response = ResponseUtils.success(200, e.getMessage(), null);
            return ResponseEntity.ok(response);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        ApiResponse<String> response = ResponseUtils.success(200, "刪除成功", null);
        return ResponseEntity.ok(response);
    }
}
