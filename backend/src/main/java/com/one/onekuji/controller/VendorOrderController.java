package com.one.onekuji.controller;

import com.one.onekuji.dto.TrackingNumberRequest;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.Order;
import com.one.onekuji.model.VendorOrderEntity;
import com.one.onekuji.repository.OrderRepository;
import com.one.onekuji.repository.VendorOrderRepository;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/vendorOrder")
public class VendorOrderController {

    @Autowired
    private VendorOrderRepository vendorOrderRepository;

    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<VendorOrderEntity>>> getAllCategory() {
        List<VendorOrderEntity> categories = vendorOrderRepository.findAll();
        if (categories == null || categories.isEmpty()) {
            ApiResponse<List<VendorOrderEntity>> response = ResponseUtils.failure(404, "無類別", null);
            return ResponseEntity.ok(response);
        }

        ApiResponse<List<VendorOrderEntity>> response = ResponseUtils.success(200, null, categories);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{orderId}")
    public ResponseEntity<ApiResponse<VendorOrderEntity>> getAll(@PathVariable String orderId) {
        VendorOrderEntity byId = vendorOrderRepository.findById(orderId);

        if (byId == null) {
            // 返回一個空的 VendorOrderEntity 而不是 404
            ApiResponse<VendorOrderEntity> response = ResponseUtils.success(200, "無類別", new VendorOrderEntity());
            return ResponseEntity.ok(response);
        }

        ApiResponse<VendorOrderEntity> response = ResponseUtils.success(200, null, byId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateOrder(@PathVariable String id, @RequestBody VendorOrderEntity vendorOrder) {
        // 在此处可以添加一些验证逻辑，比如检查 ID 和订单是否匹配
        vendorOrder.setVendorOrder(id); // 确保将 ID 设置到 vendorOrder 实体中
        vendorOrderRepository.update(vendorOrder);
        ApiResponse<VendorOrderEntity> response = ResponseUtils.success(200, null, null );
        return ResponseEntity.ok(response);
    }
    @Autowired
    private OrderRepository orderRepository;
    @PostMapping("/updateTrackingNumber")
    public ResponseEntity<ApiResponse<?>> updateTrackingNumber(@RequestBody TrackingNumberRequest request) {
        Order orderById = orderRepository.getOrderById(Long.valueOf(request.getOrderId()));
        // 查找訂單對應的 VendorOrderEntity
        VendorOrderEntity existingVendorOrder = vendorOrderRepository.findByVendorOrder(orderById.getOrderNumber());

        if (existingVendorOrder != null) {
            // 如果存在則更新訂單
            existingVendorOrder.setOrderNo(request.getTrackingNumber());
            existingVendorOrder.setErrorCode("000");
            existingVendorOrder.setErrorMessage("成功");
            existingVendorOrder.setExpress("");  // 或者從 request 中設置
            existingVendorOrder.setStatus("");  // 或者從 request 中設置

            // 更新資料庫
            vendorOrderRepository.update2(existingVendorOrder);

            ApiResponse<VendorOrderEntity> response = ResponseUtils.success(200, null,  existingVendorOrder);
            return ResponseEntity.ok(response);
        } else {
            // 如果不存在則插入新訂單
            VendorOrderEntity vendorOrderEntity = new VendorOrderEntity();
            vendorOrderEntity.setVendorOrder(orderById.getOrderNumber());
            vendorOrderEntity.setOrderNo(request.getTrackingNumber());
            vendorOrderEntity.setErrorCode("000");
            vendorOrderEntity.setErrorMessage("成功");
            vendorOrderEntity.setExpress("");
            vendorOrderEntity.setStatus("");

                // 插入資料庫，如果已存在主鍵，則會拋出錯誤
                vendorOrderRepository.insert2(vendorOrderEntity);


            return ResponseEntity.ok(ResponseUtils.success(200, "訂單插入成功", vendorOrderEntity));
        }
    }



}
