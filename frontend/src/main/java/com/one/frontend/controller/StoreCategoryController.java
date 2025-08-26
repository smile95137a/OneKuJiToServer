package com.one.frontend.controller;

import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.StoreCategory;
import com.one.frontend.service.StoreCategoryService;
import com.one.frontend.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/storeCategory")
public class StoreCategoryController {

    @Autowired
    private StoreCategoryService storeCategoryService;

    @Operation(summary = "获取所有类别", description = "检索所有商店类别的列表")
    @GetMapping("/query")
    public ResponseEntity<ApiResponse<List<StoreCategory>>> getAllCategories() {
        List<StoreCategory> categories = storeCategoryService.getAllCategories();
        if (categories == null || categories.isEmpty()) {
            ApiResponse<List<StoreCategory>> response = ResponseUtils.failure(404, "无类别", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<List<StoreCategory>> response = ResponseUtils.success(200, null, categories);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "获取类别详情", description = "通过类别 ID 获取类别的详细信息")
    @GetMapping("/query/{id}")
    public ResponseEntity<ApiResponse<StoreCategory>> getCategoryById(@PathVariable Long id) {
        StoreCategory category = storeCategoryService.getCategoryById(id);
        if (category == null) {
            ApiResponse<StoreCategory> response = ResponseUtils.failure(404, "類別不存在", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<StoreCategory> response = ResponseUtils.success(200, null, category);
        return ResponseEntity.ok(response);
    }
}
