package com.one.onekuji.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.StoreProduct;
import com.one.onekuji.request.ProductReq;
import com.one.onekuji.request.StoreProductReq;
import com.one.onekuji.response.ProductRes;
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


    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStoreProduct(@PathVariable Long id) {
        boolean isDeleted = storeProductService.deleteStoreProduct(id);
        if (!isDeleted) {
            ApiResponse<Void> response = ResponseUtils.failure(404, "未找到該商品", null);
            return ResponseEntity.ok(response);
        }
        ApiResponse<Void> response = ResponseUtils.success(200, "商品已成功刪除", null);
        return ResponseEntity.ok(response);
    }
    
    

	@PostMapping("/add2")
	public ResponseEntity<?> addStoreProduct2(@RequestBody StoreProductReq req) throws Exception {

        StoreProductRes storeProductRes = storeProductService.addStoreProduct(req);

        ApiResponse<StoreProductRes> response = ResponseUtils.success(201, null, storeProductRes);
        
		return ResponseEntity.ok(response);
	}

	@PostMapping("/update2")
	public ResponseEntity<?> updateProduct2(@RequestBody StoreProductReq req) throws Exception {
		StoreProductRes storeProductRes = storeProductService.updateStoreProduct(req.getStoreProductId(), req);
        ApiResponse<StoreProductRes> response = ResponseUtils.success(200, "商品已成功更新", storeProductRes);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/uploadProductImg")
	public ResponseEntity<ApiResponse<List<String>>> uploadProductImg(
			@RequestParam("isUseCrop") boolean isUseCrop,
			@RequestParam("storeProductId") Long storeProductId,
			@RequestParam(value = "files", required = false) List<MultipartFile> files,
			@RequestParam(value = "existingUrls", required = false) List<String> existingUrls) {
		try {
			List<String> uploadedFilePaths = new ArrayList<>();
			int[][] rwdSizes = { 
				    { 2000 , 2000 },
				};


			if (files != null && !files.isEmpty()) {
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						String[] fileUrl = ImageUtil.upload(file, rwdSizes, isUseCrop);
						uploadedFilePaths.add(fileUrl[0]);
					}
				}
			}

			if (existingUrls != null && !existingUrls.isEmpty()) {
				uploadedFilePaths.addAll(existingUrls);
			}

			storeProductService.uploadProductImg(storeProductId, uploadedFilePaths);

			ApiResponse<List<String>> response = ResponseUtils.success(200, "Files uploaded successfully",
					uploadedFilePaths);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<List<String>> response = ResponseUtils.failure(500, "Error uploading files", null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
