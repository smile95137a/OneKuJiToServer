package com.one.frontend.controller;

import com.one.frontend.model.ApiResponse;
import com.one.frontend.response.RecommendRes;
import com.one.frontend.service.ProductRecommendationMappingService;
import com.one.frontend.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendation-mapping")
public class ProductRecommendationMappingController {

    @Autowired
    private ProductRecommendationMappingService service;

    // 查詢所有商品推薦關聯
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RecommendRes>>> getAllMappings() {
        List<RecommendRes> mappings = service.getAllMappings();
        if (mappings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.failure(404, "無商品推薦關聯", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, null, mappings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<RecommendRes>>> getMappingById(@PathVariable Long id) {
        List<RecommendRes> mapping = service.getMappingById(id);
        if (mapping == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUtils.failure(400, "無效的推薦類型", null));
        }

        return ResponseEntity.ok(ResponseUtils.success(200, null, mapping));
    }



}
