package com.one.onekuji.controller;

import com.one.onekuji.model.Order;
import com.one.onekuji.request.OrderQueryReq;
import com.one.onekuji.request.OrderStatusUpdateRequest;
import com.one.onekuji.response.OrderRes;
import com.one.onekuji.response.PageRes;
import com.one.onekuji.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/query")
    public ResponseEntity<PageRes<OrderRes>> queryOrders(@RequestBody OrderQueryReq req) {
        PageRes<OrderRes> page = orderService.queryOrders(req);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/getById")
    public ResponseEntity<OrderRes> getOrderById(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        OrderRes order = orderService.getOrderById(id);
        if (order != null) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOrder(@PathVariable Long id, @RequestBody OrderStatusUpdateRequest request) {
        try {
            String resultStatus = request.getResultStatus(); // 从 JSON 中获取 resultStatus
            String s = orderService.updateOrder(id, resultStatus);
            return ResponseEntity.ok(s);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
