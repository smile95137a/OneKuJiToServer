package com.one.onekuji.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.request.StoreProductReq;
import com.one.onekuji.response.StoreProductRes;
import com.one.onekuji.service.StoreProductService;
import com.one.onekuji.util.ImageUtil;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/storeProduct")
public class StoreProductController {

    @Autowired
    private StoreProductService storeProductService;

    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<StoreProductRes>>> getAllStoreProduct() {
        List<StoreProductRes> storeProductResList = storeProductService.getAllStoreProduct();
        if (storeProductResList == null || storeProductResList.isEmpty()) {
            ApiResponse<List<StoreProductRes>> response = ResponseUtils.failure(404, "無商品", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<List<StoreProductRes>> response = ResponseUtils.success(200, null, storeProductResList);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/add")
    public ResponseEntity<ApiResponse<StoreProductRes>> addStoreProduct(
            @RequestPart("productReq") String storeProductReqJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {

        StoreProductReq storeProductReq = new ObjectMapper().readValue(storeProductReqJson, StoreProductReq.class);

        List<String> fileUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String fileUrl = ImageUtil.upload(image);
                    fileUrls.add(fileUrl);
                }
            }
        }

        storeProductReq.setImageUrl(String.join(",", fileUrls));

        StoreProductRes storeProductRes = storeProductService.addStoreProduct(storeProductReq);

        ApiResponse<StoreProductRes> response = ResponseUtils.success(201, null, storeProductRes);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping(value = "/update/{id}")
    public ResponseEntity<ApiResponse<StoreProductRes>> updateStoreProduct(
            @PathVariable Long id,
            @RequestPart("storeProductReq") String storeProductReqJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {
        StoreProductReq storeProductReq = new ObjectMapper().readValue(storeProductReqJson, StoreProductReq.class);
        if (storeProductReq == null) {
            ApiResponse<StoreProductRes> response = ResponseUtils.failure(404, "未找到該商品", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        List<String> fileUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String fileUrl = ImageUtil.upload(image);
                    fileUrls.add(fileUrl);
                }
            }
        }

        storeProductReq.setImageUrl(String.join(",", fileUrls));
        StoreProductRes storeProductRes = storeProductService.updateStoreProduct(id, storeProductReq);
        ApiResponse<StoreProductRes> response = ResponseUtils.success(200, "商品已成功更新", storeProductRes);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStoreProduct(@PathVariable Long id) {
        boolean isDeleted = storeProductService.deleteStoreProduct(id);
        if (!isDeleted) {
            ApiResponse<Void> response = ResponseUtils.failure(404, "未找到該商品", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<Void> response = ResponseUtils.success(200, "商品已成功刪除", null);
        return ResponseEntity.ok(response);
    }
}
