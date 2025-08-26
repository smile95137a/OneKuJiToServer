package com.one.frontend.controller;

import com.one.frontend.config.security.CustomUserDetails;
import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.request.UserReq;
import com.one.frontend.response.UserRes;
import com.one.frontend.service.UserService;
import com.one.frontend.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "使用者管理", description = "與使用者相關的操作")
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(summary = "通過 ID 獲取使用者", description = "根據其 ID 獲取使用者")
	@GetMapping("/getUserInfo")
	public ResponseEntity<?> getUserInfo() {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		var user = userService.getUserById(userDetails.getId());
		if (user == null) {
			return ResponseEntity.ok(ResponseUtils.failure(999, "找不到使用者", user));
		}
		return ResponseEntity.ok(ResponseUtils.success(200, null, user));
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserRes>> registerUser(@RequestBody UserReq userReq) throws Exception {
		UserRes userRes = userService.registerUser(userReq);
		ApiResponse<UserRes> response = ResponseUtils.success(201, null, userRes);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/*
	 * 只能拿到自己的 不能拿別人的 使用token去帶userId 從CustomUserDetails拿到要的資訊
	 */
	@Operation(summary = "更新現有使用者", description = "根據 ID 更新現有的使用者")
	@PutMapping("/updateUser")
	public ResponseEntity<?> updateUser(@RequestBody UserReq req) {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		var user = userService.getUserById(userDetails.getId());
		if (user == null) {
			return ResponseEntity.ok(ResponseUtils.failure(999, "找不到使用者", user));
		}
		var userId = user.getId();
		try {
			var res = userService.updateUser(req, userId);
			return ResponseEntity.ok(ResponseUtils.success(200, "更新成功", res));
		} catch (Exception e) {
			return ResponseEntity.ok(ResponseUtils.failure(999, "更新失敗", false));
		}
	}
	
	@PutMapping("/updateUserInvoice")
	public ResponseEntity<?> updateUserInvoice(@RequestBody UserReq req) {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		var user = userService.getUserById(userDetails.getId());
		if (user == null) {
			return ResponseEntity.ok(ResponseUtils.failure(999, "找不到使用者", user));
		}
		var userId = user.getId();
		try {
			var res = userService.updateUserInvoice(req, userId);
			return ResponseEntity.ok(ResponseUtils.success(200, "更新成功", res));
		} catch (Exception e) {
			return ResponseEntity.ok(ResponseUtils.failure(999, "更新失敗", false));
		}
	}


	@PutMapping("/resetPwd")
	public ResponseEntity<?> resetPwd(@RequestBody UserReq req) {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		var user = userService.getUserById(userDetails.getId());
		if (user == null) {
			return ResponseEntity.ok(ResponseUtils.failure(999, "找不到使用者", user));
		}
		var userId = user.getId();
		try {
			var res = userService.updateUser(req, userId);
			return ResponseEntity.ok(ResponseUtils.success(200, "更新成功", res));
		} catch (Exception e) {
			return ResponseEntity.ok(ResponseUtils.failure(999, "更新失敗", false));
		}
	}
}
