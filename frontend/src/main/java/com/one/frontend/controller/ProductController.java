package com.one.frontend.controller;

import com.one.frontend.model.ApiResponse;
import com.one.frontend.request.ProductQueryReq;
import com.one.frontend.response.PageRes;
import com.one.frontend.response.ProductRes;
import com.one.frontend.service.ProductService;
import com.one.frontend.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "商品分頁查詢", description = "支援分頁、模糊搜尋、篩選條件")
    @PostMapping("/query")
    public ResponseEntity<ApiResponse<PageRes<ProductRes>>> queryProducts(@RequestBody ProductQueryReq req) {
        PageRes<ProductRes> page = productService.queryProducts(req);
        return ResponseEntity.ok(ResponseUtils.success(200, null, page));
    }

    @Operation(summary = "獲取所有獎品（分頁）", description = "分頁查詢所有商品")
    @PostMapping("/all")
    public ResponseEntity<ApiResponse<PageRes<ProductRes>>> getAll(@RequestBody ProductQueryReq req) {
        PageRes<ProductRes> page = productService.queryProducts(req);
        return ResponseEntity.ok(ResponseUtils.success(200, null, page));
    }

    @Operation(summary = "獲取產品詳情", description = "通過產品 ID 獲取產品的詳細信息")
    @PostMapping("/getById")
    public ResponseEntity<ApiResponse<ProductRes>> getProductById(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        ProductRes productRes = productService.getProductById(id);
        if (productRes == null) {
            ApiResponse<ProductRes> response = ResponseUtils.failure(404, "產品不存在", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<ProductRes> response = ResponseUtils.success(200, null, productRes);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "依類型查詢產品（分頁）", description = "通過產品類型查詢產品列表")
    @PostMapping("/type")
    public ResponseEntity<ApiResponse<PageRes<ProductRes>>> getProductByType(@RequestBody ProductQueryReq req) {
        PageRes<ProductRes> page = productService.queryProducts(req);
        return ResponseEntity.ok(ResponseUtils.success(200, null, page));
    }
}
