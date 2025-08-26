package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.ProductRecommendationMapping;
import com.one.onekuji.service.ProductRecommendationMappingService;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation-mapping")
public class ProductRecommendationMappingController {

    @Autowired
    private ProductRecommendationMappingService service;

    // 查詢所有商品推薦關聯
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductRecommendationMapping>>> getAllMappings() {
        List<ProductRecommendationMapping> mappings = service.getAllMappings();
        if (mappings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.failure(404, "無商品推薦關聯", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, null, mappings));
    }

    // 根據ID查詢商品推薦關聯
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductRecommendationMapping>> getMappingById(@PathVariable Long id) {
        ProductRecommendationMapping mapping = service.getMappingById(id);
        if (mapping == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.failure(404, "商品推薦關聯未找到", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, null, mapping));
    }

    // 創建新的商品推薦關聯
    @PostMapping
    public ResponseEntity<ApiResponse<ProductRecommendationMapping>> createMapping(@RequestBody ProductRecommendationMapping mapping) {
        int result = service.createMapping(mapping);
        if (result > 0) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseUtils.success(201, "創建成功", mapping));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtils.failure(500, "創建失敗", null));
    }

    // 更新商品推薦關聯
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductRecommendationMapping>> updateMapping(@PathVariable Long id, @RequestBody ProductRecommendationMapping mapping) {
        int result = service.updateMapping(id, mapping);
        if (result > 0) {
            return ResponseEntity.ok(ResponseUtils.success(200, "更新成功", mapping));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtils.failure(404, "更新失敗，商品推薦關聯未找到", null));
    }

    // 刪除商品推薦關聯
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMapping(@PathVariable Long id) {
        int result = service.deleteMapping(id);
        if (result > 0) {
            return ResponseEntity.ok(ResponseUtils.success(200, "刪除成功", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtils.failure(404, "刪除失敗，商品推薦關聯未找到", null));
    }
}
