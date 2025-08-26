package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.StoreCategory;
import com.one.onekuji.service.CategoryService;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 查詢所有類別
    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<StoreCategory>>> getAllCategory() {
        List<StoreCategory> categories = categoryService.getAllCategory();
        if (categories == null || categories.isEmpty()) {
            ApiResponse<List<StoreCategory>> response = ResponseUtils.failure(404, "無類別", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<List<StoreCategory>> response = ResponseUtils.success(200, null, categories);
        return ResponseEntity.ok(response);
    }

    // 根據ID查詢類別
    @GetMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<StoreCategory>> getCategoryById(@PathVariable("id") Long id) {
        StoreCategory category = categoryService.getCategoryById(id);
        if (category == null) {
            ApiResponse<StoreCategory> response = ResponseUtils.failure(404, "類別未找到", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<StoreCategory> response = ResponseUtils.success(200, null, category);
        return ResponseEntity.ok(response);
    }

    // 創建新的類別
    @PostMapping
    public ResponseEntity<ApiResponse<StoreCategory>> createCategory(@RequestBody StoreCategory category) {
        StoreCategory createdCategory = categoryService.createCategory(category);
        ApiResponse<StoreCategory> response = ResponseUtils.success(201, "創建成功", createdCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 更新類別
    @PutMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<StoreCategory>> updateCategory(
            @PathVariable("id") Long id,
            @RequestBody StoreCategory category) {
        StoreCategory updatedCategory = categoryService.updateCategory(id, category);
        if (updatedCategory == null) {
            ApiResponse<StoreCategory> response = ResponseUtils.failure(404, "類別未找到", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<StoreCategory> response = ResponseUtils.success(200, "更新成功", updatedCategory);
        return ResponseEntity.ok(response);
    }

    // 刪除類別
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable("id") Long id) {
        boolean deleted = categoryService.deleteCategory(id);
        if (!deleted) {
            ApiResponse<Void> response = ResponseUtils.failure(404, "類別未找到", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<Void> response = ResponseUtils.success(200, "刪除成功", null);
        return ResponseEntity.ok(response);
    }
}
