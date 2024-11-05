package com.one.onekuji.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one.onekuji.eenum.PrizeCategory;
import com.one.onekuji.eenum.ProductType;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.request.ProductReq;
import com.one.onekuji.response.ProductRes;
import com.one.onekuji.service.ProductService;
import com.one.onekuji.util.ImageUtil;
import com.one.onekuji.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Operation(summary = "獲取所有獎品", description = "檢索所有獎品的列表")
    @GetMapping("/query")
    public ResponseEntity<ApiResponse<List<ProductRes>>> getAllProduct() {
        List<ProductRes> products = productService.getAllProduct();
        if (products == null || products.isEmpty()) {
            ApiResponse<List<ProductRes>> response = ResponseUtils.failure(404, "無類別", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<List<ProductRes>> response = ResponseUtils.success(200, null, products);
        return ResponseEntity.ok(response);
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


    @Operation(summary = "獲取所有獎品", description = "檢索所有獎品的列表")
    @PostMapping("/type")
    public ResponseEntity<ApiResponse<List<ProductRes>>> getAllProduct(@RequestBody Map<String, String> requestBody) {
        String type = requestBody.get("type");

            ProductType productType = ProductType.valueOf(type.trim().toUpperCase());
            List<ProductRes> products = productService.getAllProductByType(productType);

            if (products == null || products.isEmpty()) {
                ApiResponse<List<ProductRes>> response = ResponseUtils.failure(404, "無類別", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ApiResponse<List<ProductRes>> response = ResponseUtils.success(200, null, products);
            return ResponseEntity.ok(response);

    }

    @Operation(summary = "獲取所有獎品", description = "檢索所有獎品的列表")
    @PostMapping("/OneKuJi/type")
    public ResponseEntity<ApiResponse<List<ProductRes>>> getOneKuJiType(@RequestBody Map<String, String> requestBody) {
            String type = requestBody.get("type");
            PrizeCategory prizeCategory = PrizeCategory.valueOf(type.trim().toUpperCase());
            List<ProductRes> products = productService.getOneKuJiType(prizeCategory);
            if (products == null || products.isEmpty()) {
                ApiResponse<List<ProductRes>> response = ResponseUtils.failure(404, "無類別", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            ApiResponse<List<ProductRes>> response = ResponseUtils.success(200, null, products);
            return ResponseEntity.ok(response);
    }

    @Operation(summary = "創建產品", description = "創建一個新產品")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductRes>> createProduct(
            @RequestPart("productReq") String productReqJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS , true);
        ProductReq storeProductReq = objectMapper.readValue(productReqJson, ProductReq.class);

        List<String> fileUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String fileUrl = ImageUtil.upload(image);
                    fileUrls.add(fileUrl);
                }
            }
        }

        storeProductReq.setImageUrls(fileUrls);

        ProductRes productRes = productService.createProduct(storeProductReq);
        ApiResponse<ProductRes> response = ResponseUtils.success(201, "產品創建成功", productRes);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "更新產品", description = "更新現有產品的詳細信息")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ProductRes>> updateProduct(
            @PathVariable Long id,
            @RequestPart("productReq") String productReqJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS , true);
        ProductReq storeProductReq = objectMapper.readValue(productReqJson, ProductReq.class);
        if (storeProductReq == null) {
            ApiResponse<ProductRes> response = ResponseUtils.failure(404, "產品不存在", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        List<String> fileUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String fileUrl = ImageUtil.upload(image); // 使用 ImageUtil 上传文件
                    fileUrls.add(fileUrl);
                }
            }
        }else{
            ProductRes storeProductRes = productService.getProductById(id);
            List<String> list = storeProductRes.getImageUrls();
            fileUrls.addAll(list);
        }

        storeProductReq.setImageUrls(fileUrls);
        ProductRes productRes = productService.updateProduct(id, storeProductReq);

        ApiResponse<ProductRes> response = ResponseUtils.success(200, "產品更新成功", productRes);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "刪除產品", description = "根據產品 ID 刪除產品")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        boolean isDeleted = productService.deleteProduct(id);
        if (!isDeleted) {
            ApiResponse<Void> response = ResponseUtils.failure(404, "產品不存在", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<Void> response = ResponseUtils.success(200, "產品刪除成功", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/duplicate")
    public ResponseEntity<ApiResponse<Void>> duplicateProduct(@PathVariable Long productId) {
        ProductRes originalProduct = productService.getProductById(productId);
        if (originalProduct == null) {
            return ResponseEntity.notFound().build();
        }
        productService.duplicateProduct(originalProduct);

        ApiResponse<Void> response = ResponseUtils.success(200, "複製成功", null);
        return ResponseEntity.ok(response);
    }

}
