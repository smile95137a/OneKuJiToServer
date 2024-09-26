package com.one.onekuji.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.request.DetailReq;
import com.one.onekuji.response.DetailRes;
import com.one.onekuji.service.ProductDetailService;
import com.one.onekuji.util.ImageUtil;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/productDetail")
public class ProductDetailController {

    @Autowired
    private ProductDetailService productDetailService;

    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<DetailRes>>> getAllProductDetails() {
        List<DetailRes> productDetailResList = productDetailService.getAllProductDetails();
        if (productDetailResList == null || productDetailResList.isEmpty()) {
            ApiResponse<List<DetailRes>> response = ResponseUtils.failure(404, "無商品", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        ApiResponse<List<DetailRes>> response = ResponseUtils.success(200, null, productDetailResList);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<List<DetailRes>>> addProductDetails(
            @RequestParam("productDetailReq") String productDetailReqsJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS , true);
        List<DetailReq> detailReqs = objectMapper.readValue(productDetailReqsJson, new TypeReference<List<DetailReq>>() {});

        Map<Integer, String> imageUrlsMap = new HashMap<>();
        if (images != null && !images.isEmpty()) {
            // 上传图片并获取其 URL
            for (int i = 0; i < images.size(); i++) {
                MultipartFile image = images.get(i);
                if (!image.isEmpty() && i < detailReqs.size()) {
                    String fileUrl = ImageUtil.upload(image);
                    // 这里检查 fileUrl 是否为空字符串
                    if (fileUrl != null && !fileUrl.trim().isEmpty()) {
                        imageUrlsMap.put(i, fileUrl);
                    }
                }
            }
        }

        for (int i = 0; i < detailReqs.size(); i++) {
            DetailReq detailReq = detailReqs.get(i);
            String imageUrl = imageUrlsMap.get(i);
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                if (detailReq.getImageUrls() == null) {
                    detailReq.setImageUrls(new ArrayList<>());
                }
                detailReq.getImageUrls().add(imageUrl);
            }
        }

        List<DetailRes> detailResList = productDetailService.addProductDetails(detailReqs);
        ApiResponse<List<DetailRes>> response = ResponseUtils.success(201, null, detailResList);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @PutMapping(value = "/update/{id}")
    public ResponseEntity<ApiResponse<DetailRes>> updateProductDetail(
            @PathVariable Long id,
            @RequestPart("productDetailReq") String productDetailReqJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS , true);
        DetailReq productDetailReq = objectMapper.readValue(productDetailReqJson, DetailReq.class);

        if (productDetailReq == null) {
            ApiResponse<DetailRes> response = ResponseUtils.failure(404, "未找到該商品", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 如果有传递图片，清空现有的 imageUrls 列表
        if (images != null && !images.isEmpty()) {
            productDetailReq.setImageUrls(new ArrayList<>()); // 清空列表，确保只保留新图片
        }

        List<String> fileUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String fileUrl = ImageUtil.upload(image);
                    if (fileUrl != null && !fileUrl.trim().isEmpty()) {
                        fileUrls.add(fileUrl);
                    }
                }
            }
        }

        if (!fileUrls.isEmpty()) {
            productDetailReq.getImageUrls().addAll(fileUrls); // 只添加新上传的图片 URL
        }

        DetailRes productDetailRes = productDetailService.updateProductDetail(id, productDetailReq);
        ApiResponse<DetailRes> response = ResponseUtils.success(200, "商品已成功更新", productDetailRes);
        return ResponseEntity.ok(response);
    }



    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductDetail(@PathVariable Long id) {
        boolean isDeleted = productDetailService.deleteProductDetail(id);
        if (!isDeleted) {
            ApiResponse<Void> response = ResponseUtils.failure(404, "未找到該商品", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<Void> response = ResponseUtils.success(200, "商品已成功刪除", null);
        return ResponseEntity.ok(response);
    }
}
