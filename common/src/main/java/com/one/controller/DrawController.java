package com.one.controller;

import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.dto.DrawDto;
import com.one.frontend.dto.GachaDrawDto;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.DrawResult;
import com.one.frontend.response.DrawResponse;
import com.one.frontend.service.DrawResultService;
import com.one.frontend.service.RedemptionCodeService;
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
	private RedemptionCodeService redemptionCodeService;

	@Autowired
	private DrawResultService drawResultService;

	@PostMapping("/redeem")
	public ResponseEntity<ApiResponse<List<DrawResult>>> redeemCode(@RequestBody DrawDto drawDto) throws Exception {
		var userDetails = SecurityUtils.getCurrentUserPrinciple();
		var userId = userDetails.getId();
		String s = null;
		if(drawResultService.checkPrize(userId) && drawResultService.checkStatus(drawDto.getProductId())){
			s = redemptionCodeService.redeemCode(userId , drawDto);

		}else{
			if(!drawResultService.checkPrize(userId)){
				ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "賞品盒數量大於150個，請先整理賞品盒", null);
				return ResponseEntity.ok(response);
			}else if(!drawResultService.checkStatus(drawDto.getProductId())){
				ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "大賞已完售，不可再次抽獎", null);
				return ResponseEntity.ok(response);
			}

		}
		if("兌換成功".equals(s)){
			List<DrawResult> results = drawResultService.handleDraw2(userId, drawDto.getProductId(), drawDto.getPrizeNumbers(), "1");
			ApiResponse<List<DrawResult>> response1 = ResponseUtils.success(200, s, results);
			return ResponseEntity.ok(response1);
		}else{
			ApiResponse<List<DrawResult>> response1 = ResponseUtils.failure(200, s, null);
			return ResponseEntity.ok(response1);
		}
	}


	@PostMapping("/oneprize")
	@Operation(summary = "扭蛋抽獎")
	public ResponseEntity<ApiResponse<List<DrawResult>>> drawPrize(@RequestBody GachaDrawDto gachaDrawDto) throws Exception {

		var userDetails = SecurityUtils.getCurrentUserPrinciple();
		Long userId = null;
		if(userDetails != null){
			 userId = userDetails.getId();
		}
		try {
			if(drawResultService.checkPrize(userId) && drawResultService.checkStatus(gachaDrawDto.getProductId())){
				List<DrawResult> result = drawResultService.handleDraw(userId , gachaDrawDto.getProductId());
				ApiResponse<List<DrawResult>> response = ResponseUtils.success(200, null, result);
				return ResponseEntity.ok(response);
			}else{
				if(!drawResultService.checkPrize(userId)){
					ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "賞品盒數量大於150個，請先整理賞品盒", null);
					return ResponseEntity.ok(response);
				}else if(!drawResultService.checkStatus(gachaDrawDto.getProductId())){
					ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "大賞已完售，不可再次抽獎", null);
					return ResponseEntity.ok(response);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "抽獎失敗", null);
			return ResponseEntity.ok(response);
		}
        return null;
    }

	@GetMapping("/status/{productId}")
	@Operation(summary = "檢視抽況", description = "根据产品ID获取所有奖项状态")
	public ResponseEntity<ApiResponse<DrawResponse>> getDrawStatus(@PathVariable Long productId) {
		DrawResponse prizes = drawResultService.getAllPrizes(productId);
		if (prizes == null) {
			ApiResponse<DrawResponse> response = ResponseUtils.failure(404, "沒有此ID", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		ApiResponse<DrawResponse> response = ResponseUtils.success(200, null, prizes);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/execute")
	@Operation(summary = "一番賞、盲盒抽獎", description = "根据产品ID和用户ID执行抽奖")
	public ResponseEntity<ApiResponse<List<DrawResult>>> executeDraw(@RequestBody DrawDto drawDto) {
		var userDetails = SecurityUtils.getCurrentUserPrinciple();
		var userId = userDetails.getId();
		try {
			if(drawResultService.checkPrize(userId) && drawResultService.checkStatus(drawDto.getProductId())){
			List<DrawResult> drawResult = drawResultService.handleDraw2(userId, drawDto.getProductId(), drawDto.getPrizeNumbers() , drawDto.getExchangeType());
			ApiResponse<List<DrawResult>> response = ResponseUtils.success(200, null, drawResult);
			return ResponseEntity.ok(response);
			}else{
				if(!drawResultService.checkPrize(userId)){
					ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "賞品盒數量大於150個，請先整理賞品盒", null);
					return ResponseEntity.ok(response);
				}else if(!drawResultService.checkStatus(drawDto.getProductId())){
					ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "大賞已完售，不可再次抽獎", null);
					return ResponseEntity.ok(response);
				}

			}
		} catch (Exception e) {
			ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, e.getMessage(), null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
        return null;
    }

	@PostMapping("/random/{productId}")
	@Operation(summary = "紅利抽獎(隨機制)", description = "为特定产品和用户执行随机抽奖")
	public ResponseEntity<ApiResponse<?>> executeRandom(@PathVariable Long productId,
			@RequestParam String userUid) {
		var userDetails = SecurityUtils.getCurrentUserPrinciple();
		var userId = userDetails.getId();
		try {
			if(drawResultService.checkPrize(userId) && drawResultService.checkStatus(productId)){
			DrawResult drawResult = drawResultService.handleDrawRandom(userId, productId);
			ApiResponse<DrawResult> response = ResponseUtils.success(200, null, drawResult);
			return ResponseEntity.ok(response);
		}else{
				if(!drawResultService.checkPrize(userId)){
					ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "賞品盒數量大於150個，請先整理賞品盒", null);
					return ResponseEntity.ok(response);
				}else if(!drawResultService.checkStatus(productId)){
					ApiResponse<List<DrawResult>> response = ResponseUtils.failure(400, "大賞已完售，不可再次抽獎", null);
					return ResponseEntity.ok(response);
				}

		}
		} catch (Exception e) {
			ApiResponse<DrawResult> response = ResponseUtils.failure(400, "抽奖失败", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
        return null;
    }

}
