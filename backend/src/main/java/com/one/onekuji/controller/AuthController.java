package com.one.onekuji.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.one.onekuji.dto.JWTAuthResponse;
import com.one.onekuji.dto.LoginDto;
import com.one.onekuji.dto.LoginResponse;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.User;
import com.one.onekuji.service.AuthService;
import com.one.onekuji.util.ResponseUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JWTAuthResponse>> authenticate(@RequestBody LoginDto loginDto) throws Exception {
        LoginResponse loginResponse = authService.login(loginDto);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(loginResponse.getToken());

        User user = new User();
        user.setId(loginResponse.getId());
        user.setUsername(loginResponse.getUsername());
        jwtAuthResponse.setUser(user);
        ApiResponse<JWTAuthResponse> response = ResponseUtils.success(200, null, jwtAuthResponse);

        return ResponseEntity.ok(response);
    }
}