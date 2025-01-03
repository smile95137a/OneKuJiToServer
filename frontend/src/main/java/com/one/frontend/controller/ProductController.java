package com.one.frontend.controller;

import com.one.frontend.model.ApiResponse;
import com.one.frontend.response.ProductDetailRes;
import com.one.frontend.response.ProductRes;
import com.one.frontend.service.ProductDetailService;
import com.one.frontend.service.ProductService;
import com.one.frontend.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDetailService productDetailService;

    @Operation(summary = "獲取所有獎品", description = "檢索所有獎品的列表")
    @GetMapping("/query")
    public ResponseEntity<ApiResponse<List<ProductRes>>> getAllProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        // 加入參數驗證
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest()
                    .body(ResponseUtils.failure(400, "無效的分頁參數", new ArrayList<>()));
        }

        try {
            List<ProductRes> products = productService.getAllProduct(page, size);
            if (products.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseUtils.success(200, "無類別", new ArrayList<>()));
            }

            // 處理每個商品的詳情
            CompletableFuture<List<ProductDetailRes>> futureDetails = CompletableFuture.supplyAsync(() -> {
                List<Long> productIds = products.stream()
                        .map(product -> Long.valueOf(product.getProductId()))
                        .collect(Collectors.toList());
                return productService.getProductDetailsByProductIds(productIds);
            });

            // 等待詳情處理完成（如果需要的話）
            // List<ProductDetailRes> details = futureDetails.get();  // 如果需要等待結果

            return ResponseEntity.ok(ResponseUtils.success(200, null, products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.failure(500, "系統錯誤", null));
        }
    }

    @Operation(summary = "獲取所有獎品", description = "檢索所有獎品的列表")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductRes>>> getAll() {

        // 加入參數驗證
        try {
            List<ProductRes> products = productService.getAll();
            if (products.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseUtils.success(200, "無類別", new ArrayList<>()));
            }

            // 處理每個商品的詳情
            CompletableFuture<List<ProductDetailRes>> futureDetails = CompletableFuture.supplyAsync(() -> {
                List<Long> productIds = products.stream()
                        .map(product -> Long.valueOf(product.getProductId()))
                        .collect(Collectors.toList());
                return productService.getProductDetailsByProductIds(productIds);
            });

            return ResponseEntity.ok(ResponseUtils.success(200, null, products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtils.failure(500, e.getMessage(), null));
        }
    }


    @Operation(summary = "獲取產品詳情", description = "通過產品 ID 獲取產品的詳細信息")
    @GetMapping("/query/{id}")
    public ResponseEntity<ApiResponse<ProductRes>> getProductById(@PathVariable Long id) {
        ProductRes productRes = productService.getProductById(id);
        if (productRes == null) {
            ApiResponse<ProductRes> response = ResponseUtils.failure(404, "產品不存在", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<ProductRes> response = ResponseUtils.success(200, null, productRes);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "獲取產品詳情", description = "通過產品 ID 獲取產品的詳細信息")
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<ProductRes>>> getProductByType(@PathVariable String type) {
        List<ProductRes> productRes = productService.getProductByType(type);
        if (productRes == null) {
            ApiResponse<List<ProductRes>> response = ResponseUtils.failure(404, "產品不存在", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<List<ProductRes>> response = ResponseUtils.success(200, null, productRes);
        return ResponseEntity.ok(response);
    }

}
