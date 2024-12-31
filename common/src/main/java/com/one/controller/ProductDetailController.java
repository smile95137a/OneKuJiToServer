package com.one.controller;

import com.one.frontend.model.ApiResponse;
import com.one.frontend.response.ProductDetailRes;
import com.one.frontend.service.ProductDetailService;
import com.one.frontend.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/productDetail")
public class ProductDetailController {

    @Autowired
    private ProductDetailService productDetailService;

    @Operation(summary = "獲取所有獎品", description = "檢索所有獎品的列表")
    @GetMapping("/query")
    public ResponseEntity<ApiResponse<List<ProductDetailRes>>> getAllProduct() {
        List<ProductDetailRes> products = productDetailService.getAllProductDetail();
        if (products == null || products.isEmpty()) {
            ApiResponse<List<ProductDetailRes>> response = ResponseUtils.failure(404, null, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<List<ProductDetailRes>> response = ResponseUtils.success(200, null, products);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/query/{productId}")
    public ResponseEntity<ApiResponse<List<ProductDetailRes>>> getProductById(
            @Parameter(description = "獎品的 ID", example = "1") @PathVariable Long productId) {
        List<ProductDetailRes> products = productDetailService.getProductDetailByProductId(productId);
        if (products == null || products.isEmpty()) {
            ApiResponse<List<ProductDetailRes>> response = ResponseUtils.failure(999, "此商品已下架或全售完，無法查看此商品", null);
            return ResponseEntity.ok(response);
        }

        ApiResponse<List<ProductDetailRes>> response = ResponseUtils.success(200, null, products);
        return ResponseEntity.ok(response);
    }

}
