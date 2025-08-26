package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.StoreProductRecommendation;
import com.one.onekuji.service.ProductRecommendationService;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
public class ProductRecommendationController {

    @Autowired
    private ProductRecommendationService recommendationService;

    // 查詢所有推薦類別
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StoreProductRecommendation>>> getAllRecommendations() {
        List<StoreProductRecommendation> recommendations = recommendationService.getAllRecommendations();
        if (recommendations == null || recommendations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtils.failure(404, "無推薦類別", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, null, recommendations));
    }

    // 根據 ID 查詢推薦類別
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StoreProductRecommendation>> getRecommendationById(@PathVariable("id") Long id) {
        StoreProductRecommendation recommendation = recommendationService.getRecommendationById(id);
        if (recommendation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtils.failure(404, "推薦類別未找到", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, null, recommendation));
    }

    // 創建推薦類別
    @PostMapping
    public ResponseEntity<ApiResponse<StoreProductRecommendation>> createRecommendation(@RequestBody StoreProductRecommendation recommendation) {
        StoreProductRecommendation createdRecommendation = recommendationService.createRecommendation(recommendation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.success(201, "創建成功", createdRecommendation));
    }

    // 更新推薦類別
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StoreProductRecommendation>> updateRecommendation(
            @PathVariable("id") Long id, @RequestBody StoreProductRecommendation recommendation) {
        StoreProductRecommendation updatedRecommendation = recommendationService.updateRecommendation(id, recommendation);
        if (updatedRecommendation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtils.failure(404, "推薦類別未找到", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, "更新成功", updatedRecommendation));
    }

    // 刪除推薦類別
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecommendation(@PathVariable("id") Long id) {
        boolean deleted = recommendationService.deleteRecommendation(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtils.failure(404, "推薦類別未找到", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, "刪除成功", null));
    }
}
