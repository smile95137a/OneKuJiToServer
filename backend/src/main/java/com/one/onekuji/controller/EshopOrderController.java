package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.EshopOrderEntity;
import com.one.onekuji.repository.EshopOrderRepository;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eshopOrder")
public class EshopOrderController {

    @Autowired
    private EshopOrderRepository eshopOrderRepository;

    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<EshopOrderEntity>>> getAllCategory() {
        List<EshopOrderEntity> categories = eshopOrderRepository.findAll();
        if (categories == null || categories.isEmpty()) {
            ApiResponse<List<EshopOrderEntity>> response = ResponseUtils.failure(404, "無類別", null);
            return ResponseEntity.ok(response);
        }

        ApiResponse<List<EshopOrderEntity>> response = ResponseUtils.success(200, null, categories);
        return ResponseEntity.ok(response);
    }
}
