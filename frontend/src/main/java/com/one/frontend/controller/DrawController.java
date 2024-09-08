package com.one.frontend.controller;

import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.DrawResult;
import com.one.frontend.model.PrizeNumber;
import com.one.frontend.service.DrawResultService;
import com.one.frontend.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/draw")
@Tag(name = "DrawController", description = "抽奖相关操作")
public class DrawController {

	@Autowired
	private DrawResultService drawResultService;

	@PostMapping("/oneprize/{userUid}")
	@Operation(summary = "扭蛋抽獎")
	public ResponseEntity<ApiResponse<List<DrawResult>>> drawPrize(@PathVariable String userUid,
			@RequestParam Integer count, @RequestParam Integer productId) throws Exception {

		var userDetails = SecurityUtils.getCurrentUserPrinciple();
		Long userId = null;
		if(userDetails != null){
			 userId = userDetails.getId();
		}
		try {
			List<DrawResult> result = drawResultService.handleDraw(userId, count, productId);
			ApiResponse<List<DrawResult>> response = ResponseUtils.success(200, null, result);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "抽獎失敗", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@GetMapping("/status/{productId}")
	@Operation(summary = "檢視抽況", description = "根据产品ID获取所有奖项状态")
	public ResponseEntity<ApiResponse<List<PrizeNumber>>> getDrawStatus(@PathVariable Long productId) {
		List<PrizeNumber> prizes = drawResultService.getAllPrizes(productId);
		if (prizes == null) {
			ApiResponse<List<PrizeNumber>> response = ResponseUtils.failure(404, "沒有此ID", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		ApiResponse<List<PrizeNumber>> response = ResponseUtils.success(200, null, prizes);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/execute/{productId}")
	@Operation(summary = "一番賞、盲盒抽獎", description = "根据产品ID和用户ID执行抽奖")
	public ResponseEntity<ApiResponse<List<DrawResult>>> executeDraw(@PathVariable Long productId,
			@RequestParam String userUid, @RequestParam List<String> prizeNumber) {
		var userDetails = SecurityUtils.getCurrentUserPrinciple();
		var userId = userDetails.getId();
		try {
			List<DrawResult> drawResult = drawResultService.handleDraw2(userId, productId, prizeNumber);
			ApiResponse<List<DrawResult>> response = ResponseUtils.success(200, null, drawResult);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, e.getMessage(), null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@PostMapping("/random/{productId}")
	@Operation(summary = "紅利抽獎(隨機制)", description = "为特定产品和用户执行随机抽奖")
	public ResponseEntity<ApiResponse<DrawResult>> executeRandom(@PathVariable Long productId,
			@RequestParam String userUid) {
		var userDetails = SecurityUtils.getCurrentUserPrinciple();
		var userId = userDetails.getId();
		try {
			DrawResult drawResult = drawResultService.handleDrawRandom(userId, productId);
			ApiResponse<DrawResult> response = ResponseUtils.success(200, null, drawResult);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			ApiResponse<DrawResult> response = ResponseUtils.failure(400, "抽奖失败", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
}
