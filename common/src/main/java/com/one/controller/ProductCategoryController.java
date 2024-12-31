package com.one.controller;

import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.ProductCategory;
import com.one.frontend.service.ProductCategoryService;
import com.one.frontend.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productCategory")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    // 查詢所有類別
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductCategory>>> getAllCategories() {
        List<ProductCategory> categories = productCategoryService.getAllCategories();

        // 过滤掉 categoryId 为 0 的类别
        List<ProductCategory> filteredCategories = categories.stream()
                .filter(category -> category.getCategoryId() != 0) // 根据实际的 getter 方法替换
                .collect(Collectors.toList());

        if (filteredCategories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.failure(404, "無類別", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, null, filteredCategories));
    }

    // 根據ID查詢類別
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<ProductCategory>> getCategoryById(@PathVariable("uuid") String uuid) {
        ProductCategory category = productCategoryService.getCategoryById(uuid);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.failure(404, "類別未找到", null));
        }
        return ResponseEntity.ok(ResponseUtils.success(200, null, category));
    }


}
