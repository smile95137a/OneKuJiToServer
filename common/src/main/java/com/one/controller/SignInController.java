package com.one.controller;

import com.one.config.security.CustomUserDetails;
import com.one.config.security.SecurityUtils;
import com.one.model.ApiResponse;
import com.one.model.SignIn;
import com.one.response.SignInRes;
import com.one.service.SignInService;
import com.one.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/signIn")
public class SignInController {

	@Autowired
	private SignInService service;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<SignIn>>> getAllSignIns() {
		List<SignIn> signInList = service.getAllSignIns();
		ApiResponse<List<SignIn>> response = ResponseUtils.success(200, null, signInList);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/draw")
	public ResponseEntity<?> spinWheel() {
		CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();

		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		var userId = userDetails.getId();
		SignInRes signInList;
		try {
			signInList = service.spinWheel(userId);
		} catch (Exception e) {
		    var res = ResponseUtils.failure(999, e.getMessage(), false);
            return ResponseEntity.ok(res);
		}
		var res = ResponseUtils.success(200, null, signInList);
		return ResponseEntity.ok(res);
	}

}
