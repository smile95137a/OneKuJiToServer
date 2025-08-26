package com.one.controller;

import com.one.config.security.CustomUserDetails;
import com.one.config.security.SecurityUtils;
import com.one.model.ApiResponse;
import com.one.response.StoreProductRes;
import com.one.service.StoreProductService;
import com.one.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storeProduct")
public class StoreProductController {

	@Autowired
	private StoreProductService storeProductService;

	@Operation(summary = "獲取所有產品", description = "檢索所有產品的列表")
	@GetMapping("/query")
	public ResponseEntity<?> getAllStoreProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "6") int size) {
		List<StoreProductRes> products = storeProductService.getAllStoreProducts(page, size);
		if (products == null || products.isEmpty()) {
			var res = ResponseUtils.failure(999, "無產品", null);
			return ResponseEntity.ok(res);
		}

		var res = ResponseUtils.success(000, null, products);
		return ResponseEntity.ok(res);
	}

	@GetMapping("/query/{productCode}")
	public ResponseEntity<?> getProductById(@PathVariable String productCode) {
		StoreProductRes productRes = storeProductService.getStoreProductByProductCode(productCode);
		if (productRes == null) {
			ApiResponse<StoreProductRes> response = ResponseUtils.failure(404, "產品不存在", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();

		if (userDetails == null) {
			productRes.setFavorited(false);
		} else {
			var userId = userDetails.getId();
			boolean isFavorited = storeProductService.isProductFavoritedByUser(userId, productCode);
			productRes.setFavorited(isFavorited);
		}

		ApiResponse<StoreProductRes> response = ResponseUtils.success(200, null, productRes);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{productCode}/popularity")
	public ResponseEntity<?> updateProductPopularity(@PathVariable String productCode) {
		boolean success = storeProductService.updateProductPopularity(productCode);
		if (success) {
			var response = ResponseUtils.success(200, "產品熱度更新成功", null);
			return ResponseEntity.ok(response);
		} else {
			var response = ResponseUtils.failure(400, "產品熱度更新失敗", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@PostMapping("/{productCode}/favorite")
	public ResponseEntity<?> toggleFavorite(@PathVariable String productCode) {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
		var userId = userDetails.getId();
		boolean success = storeProductService.toggleFavorite(userId, productCode);
		var response = ResponseUtils.success(200, "收藏狀態更新成功", success);
		return ResponseEntity.ok(response);
	}
}
