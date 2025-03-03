package com.one.onekuji.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one.onekuji.dto.ProductDetailDTO;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.request.DetailReq;
import com.one.onekuji.request.ProductReq;
import com.one.onekuji.response.DetailRes;
import com.one.onekuji.response.ProductDetailRes;
import com.one.onekuji.response.ProductRes;
import com.one.onekuji.service.ProductDetailService;
import com.one.onekuji.util.ImageUtil;
import com.one.onekuji.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
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
        try {
            List<DetailRes> productDetailResList = productDetailService.getAllProductDetails();
//            DetailRes product = productDetailService.getAllProductDetailsByProductId(productDetailResList.get(0).getProductId());
//            if(product != null){
//                productDetailResList.add(product);
//            }
            if (productDetailResList == null || productDetailResList.isEmpty()) {
                ApiResponse<List<DetailRes>> response = ResponseUtils.failure(404, "無商品", null);
                return ResponseEntity.ok(response);
            }

            ApiResponse<List<DetailRes>> response = ResponseUtils.success(200, null, productDetailResList);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
        }
       return null;
    }




    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductDetail(@PathVariable Long id) {
        boolean isDeleted = productDetailService.deleteProductDetail(id);
        if (!isDeleted) {
            ApiResponse<Void> response = ResponseUtils.failure(404, "未找到該商品", null);
            return ResponseEntity.ok(response);
        }
        ApiResponse<Void> response = ResponseUtils.success(200, "商品已成功刪除", null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "獲取產品詳情", description = "通過產品 ID 獲取產品的詳細信息")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailRes>> getProductById(@PathVariable Long productId) {
        ProductDetailRes productRes = productDetailService.getProductById(productId);
        if (productRes == null) {
            ApiResponse<ProductDetailRes> response = ResponseUtils.failure(404, "產品不存在", null);
            return ResponseEntity.ok(response);
        }

        ApiResponse<ProductDetailRes> response = ResponseUtils.success(200, null, productRes);
        return ResponseEntity.ok(response);
    }
    


    @PostMapping("/add2")
    public ResponseEntity<?> addProductDetails2(@RequestBody DetailReq req) throws IOException {
      
        List<DetailRes> detailResList = productDetailService.addProductDetails(List.of(req));
        ApiResponse<DetailRes> response = ResponseUtils.success(201, null, detailResList.get(0));
        return ResponseEntity.ok(response);
    }
    
	@PostMapping("/update2")
	public ResponseEntity<?> updateProductDetails2(@RequestBody DetailReq req) throws Exception {
		  DetailRes productDetailRes = productDetailService.updateProductDetail(Long.valueOf(req.getProductDetailId()), req);
          ApiResponse<DetailRes> response = ResponseUtils.success(200, "商品已成功更新", productDetailRes);
		return ResponseEntity.ok(response);
	}

    
	@PostMapping("/uploadProductDetailImg")
	public ResponseEntity<ApiResponse<List<String>>> uploadProductDetailImg(@RequestParam("productDetailId") Long productDetailId,
			@RequestParam(value = "files", required = false) List<MultipartFile> files,
			@RequestParam(value = "existingUrls", required = false) List<String> existingUrls) {
		try {
			List<String> uploadedFilePaths = new ArrayList<>();
			List<String> uploadedFilePathsLG = new ArrayList<>();
			List<String> uploadedFilePathsMD = new ArrayList<>();
			List<String> uploadedFilePathsXS = new ArrayList<>();
			int[][] rwdSizes = { 
					{ 1920, 1080 },
					{ 1280, 720 }, 
					{ 1024, 768 },
					{ 750, 750 },
			};

			if (files != null && !files.isEmpty()) {
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						String[] fileUrl = ImageUtil.upload(file, rwdSizes);
						uploadedFilePaths.add(fileUrl[0]);
						uploadedFilePathsLG.add(fileUrl[1]);
						uploadedFilePathsMD.add(fileUrl[2]);
						uploadedFilePathsXS.add(fileUrl[3]);
					}
				}
			}

			if (existingUrls != null && !existingUrls.isEmpty()) {
				uploadedFilePaths.addAll(existingUrls);
				uploadedFilePathsLG.addAll(existingUrls.stream()
						.map(url -> url.replace(String.format("%sx%s", rwdSizes[0][0], rwdSizes[0][1]),
								String.format("%sx%s", rwdSizes[1][0], rwdSizes[1][1])))
						.toList());
				uploadedFilePathsMD.addAll(existingUrls.stream()
						.map(url -> url.replace(String.format("%sx%s", rwdSizes[2][0], rwdSizes[2][1]),
								String.format("%sx%s", rwdSizes[2][0], rwdSizes[2][1])))
						.toList());
				uploadedFilePathsXS.addAll(existingUrls.stream()
						.map(url -> url.replace(String.format("%sx%s", rwdSizes[3][0], rwdSizes[3][1]),
								String.format("%sx%s", rwdSizes[3][0], rwdSizes[3][1])))
						.toList());
			}
			productDetailService.uploadProductDetailImg(productDetailId, uploadedFilePaths, uploadedFilePathsLG, uploadedFilePathsMD,
					uploadedFilePathsXS);


			ApiResponse<List<String>> response = ResponseUtils.success(200, "Files uploaded successfully",
					uploadedFilePaths);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<List<String>> response = ResponseUtils.failure(500, "Error uploading files", null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

    @PutMapping(value = "/updateDetail/{id}")
    public ResponseEntity<ApiResponse<DetailRes>> updateProductDetail(
            @PathVariable Long id,
            @RequestBody ProductDetailDTO productDetailDTO
            ) throws IOException {
        try {
            DetailRes productDetailRes = productDetailService.updateProductDTO(id, productDetailDTO);
            ApiResponse<DetailRes> response = ResponseUtils.success(200, "商品已成功更新", productDetailRes);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
