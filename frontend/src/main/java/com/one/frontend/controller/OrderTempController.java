package com.one.frontend.controller;

import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.Order;
import com.one.frontend.model.OrderTemp;
import com.one.frontend.service.OrderTempService;
import com.one.frontend.util.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-temp")
public class OrderTempController {

    private final OrderTempService orderTempService;

    public OrderTempController(OrderTempService orderTempService) {
        this.orderTempService = orderTempService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addOrderTemp(@RequestBody Order orderTemp) {
        try {
            orderTempService.addOrderTemp(orderTemp);
            var response = ResponseUtils.success(201, "訂單成功創建", orderTemp);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            var response = ResponseUtils.failure(500, "訂單創建失敗", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getOrderTempById(@PathVariable Integer id) {
        try {
            var result = orderTempService.getOrderTempById(id);
            var response = ResponseUtils.success(200, "訂單查詢成功", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            var response = ResponseUtils.failure(404, "未找到該訂單", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllOrderTemps() {
        try {
            List<OrderTemp> orderTemps = orderTempService.getAllOrderTemps();
            var response = ResponseUtils.success(200, "訂單查詢成功", orderTemps);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            var response = ResponseUtils.failure(500, "查詢訂單失敗", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateOrderTemp(@RequestBody OrderTemp orderTemp) {
        try {
            orderTempService.updateOrderTemp(orderTemp);
            var response = ResponseUtils.success(200, "訂單更新成功", orderTemp);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            var response = ResponseUtils.failure(500, "訂單更新失敗", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteOrderTemp(@PathVariable Integer id) {
        try {
            orderTempService.deleteOrderTemp(id);
            var response = ResponseUtils.success(200, "訂單刪除成功", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            var response = ResponseUtils.failure(500, "訂單刪除失敗", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
