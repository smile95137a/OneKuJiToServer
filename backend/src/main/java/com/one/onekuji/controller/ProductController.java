package com.one.onekuji.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one.onekuji.dto.ProductDTO;
import com.one.onekuji.eenum.PrizeCategory;
import com.one.onekuji.eenum.ProductType;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.Product;
import com.one.onekuji.request.ProductReq;
import com.one.onekuji.response.ProductRes;
import com.one.onekuji.service.ProductService;
import com.one.onekuji.util.ImageUtil;
import com.one.onekuji.util.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/product")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Operation(summary = "獲取所有獎品", description = "檢索所有獎品的列表")
	@GetMapping("/query")
	public ResponseEntity<ApiResponse<List<ProductRes>>> getAllProduct() {
		List<ProductRes> products = productService.getAllProduct();
		if (products == null || products.isEmpty()) {
			ApiResponse<List<ProductRes>> response = ResponseUtils.failure(404, "無類別", null);
			return ResponseEntity.ok(response);
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
			return ResponseEntity.ok(response);
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
			return ResponseEntity.ok(response);
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
			return ResponseEntity.ok(response);
		}

		ApiResponse<List<ProductRes>> response = ResponseUtils.success(200, null, products);
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

	@PostMapping("/add2")
	public ResponseEntity<?> createProduct2(@RequestBody ProductReq req) throws IOException {
		ProductRes productRes = productService.createProduct(req);
		ApiResponse<ProductRes> response = ResponseUtils.success(201, "產品創建成功", productRes);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/update2")
	public ResponseEntity<?> updateProduct2(@RequestBody ProductReq req) throws IOException {
		ProductRes productRes = productService.updateProduct(req.getProductId(), req);
		ApiResponse<ProductRes> response = ResponseUtils.success(201, "產品創建成功", productRes);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/uploadProductImg")
	public ResponseEntity<ApiResponse<List<String>>> uploadProductImg(@RequestParam("productId") Long productId,
			@RequestParam(value = "files", required = false) List<MultipartFile> files,
			@RequestParam(value = "existingUrls", required = false) List<String> existingUrls) {
		try {
			List<String> uploadedFilePaths = new ArrayList<>();
			List<String> uploadedFilePathsLG = new ArrayList<>();
			List<String> uploadedFilePathsMD = new ArrayList<>();
			List<String> uploadedFilePathsXS = new ArrayList<>();
			int[][] rwdSizes = { 
					 { 1024, 560 },
					    { 540, 295 }, 
					    { 360, 197 },
					    { 140, 77 },
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

			productService.uploadProductImg(productId, uploadedFilePaths, uploadedFilePathsLG, uploadedFilePathsMD,
					uploadedFilePathsXS);

			ApiResponse<List<String>> response = ResponseUtils.success(200, "Files uploaded successfully",
					uploadedFilePaths);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<List<String>> response = ResponseUtils.failure(500, "Error uploading files", null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/uploadProductBannerImg")
	public ResponseEntity<ApiResponse<List<String>>> uploadProductBannerImg(@RequestParam("productId") Long productId,
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

			productService.uploadProductBannerImg(productId, uploadedFilePaths, uploadedFilePathsLG, uploadedFilePathsMD,
					uploadedFilePathsXS);

			ApiResponse<List<String>> response = ResponseUtils.success(200, "Files uploaded successfully",
					uploadedFilePaths);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<List<String>> response = ResponseUtils.failure(500, "Error uploading files", null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@Operation(summary = "更新產品", description = "更新現有產品的詳細信息")
	@PutMapping("/updateProduct/{id}")
	public ResponseEntity<ApiResponse<Product>> updateProduct2(@PathVariable Long id,
			@RequestBody ProductDTO productDTO) {
		try {

			// 调用 service 更新产品
			Product productRes = productService.updateProduct(id, productDTO);

			ApiResponse<Product> response = ResponseUtils.success(200, "產品更新成功", productRes);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
