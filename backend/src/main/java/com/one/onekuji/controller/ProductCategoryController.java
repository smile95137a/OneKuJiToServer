package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.ProductCategory;
import com.one.onekuji.service.ProductCategoryService;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productCategory")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    // 查詢所有類別
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductCategory>>> getAllCategories() {
        List<ProductCategory> categories = productCategoryService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.failure(404, "無類別", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, null, categories));
    }

    // 根據ID查詢類別
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategory>> getCategoryById(@PathVariable("id") Long id) {
        ProductCategory category = productCategoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.failure(404, "類別未找到", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, null, category));
    }

    // 創建新類別
    @PostMapping
    public ResponseEntity<ApiResponse<ProductCategory>> createCategory(@RequestBody ProductCategory category) {
        ProductCategory createdCategory = productCategoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.success(201, "創建成功", createdCategory));
    }

    // 更新類別
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategory>> updateCategory(
            @PathVariable("id") Long id,
            @RequestBody ProductCategory category) {
        ProductCategory updatedCategory = productCategoryService.updateCategory(id, category);
        if (updatedCategory == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.failure(404, "類別未找到", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, "更新成功", updatedCategory));
    }

    // 刪除類別
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable("id") Long id) {
        boolean deleted = productCategoryService.deleteCategory(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.failure(404, "類別未找到", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, "刪除成功", null));
    }
}
