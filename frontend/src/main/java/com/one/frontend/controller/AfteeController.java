// AfteeController.java
package com.one.frontend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.one.frontend.util.ResponseUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/aftee")
@RequiredArgsConstructor
public class AfteeController {

	private final AfteeService afteeService;

	@PostMapping("/genPreRegister")
	public ResponseEntity<?> genPreRegister(@RequestBody GenPreRegisterReq req) {
		try {
			var payload = afteeService.generatePreRegisterPayload(req.getType(), req.getOrderNo(), req.getReturnUrl());
			var afteeResponse = afteeService.sendPreRegisterToAftee(payload);
			return ResponseEntity.ok(ResponseUtils.success(200, "", afteeResponse));
		} catch (Exception e) {
			log.error("AFTEE 處理失敗", e);
			return ResponseEntity.status(500).body(ResponseUtils.error(500, "AFTEE 發送失敗：" + e.getMessage()));
		}
	}
}
