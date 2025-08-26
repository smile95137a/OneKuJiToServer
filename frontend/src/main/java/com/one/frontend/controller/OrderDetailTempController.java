package com.one.frontend.controller;

import com.one.frontend.model.OrderDetail;
import com.one.frontend.model.OrderDetailTemp;
import com.one.frontend.service.OrderDetailTempService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailTempController {

    private final OrderDetailTempService orderDetailTempService;

    public OrderDetailTempController(OrderDetailTempService orderDetailTempService) {
        this.orderDetailTempService = orderDetailTempService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailTemp> getOrderDetailById(@PathVariable Long id) {
        OrderDetailTemp orderDetail = orderDetailTempService.getOrderDetailById(id);
        return ResponseEntity.ok(orderDetail);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<?>> getOrderDetailsByOrderId(@PathVariable Long orderId) {
        List<OrderDetailTemp> orderDetails = orderDetailTempService.getOrderDetailsByOrderId(orderId);
        return ResponseEntity.ok(orderDetails);
    }

    @PostMapping
    public ResponseEntity<?> createOrderDetail(@RequestBody OrderDetail orderDetail) {
        orderDetailTempService.insertOrderDetail(orderDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@PathVariable Long id, @RequestBody OrderDetailTemp orderDetail) {
        orderDetail.setId(id);
        orderDetailTempService.updateOrderDetail(orderDetail);
        return ResponseEntity.ok(orderDetail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long id) {
        orderDetailTempService.deleteOrderDetail(id);
        return ResponseEntity.noContent().build();
    }
}
