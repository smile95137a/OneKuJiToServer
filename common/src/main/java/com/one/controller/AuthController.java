package com.one.controller;

import com.one.frontend.dto.JWTAuthResponse;
import com.one.frontend.dto.LoginDto;
import com.one.frontend.dto.LoginResponse;
import com.one.frontend.exception.AllException;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.User;
import com.one.frontend.service.AuthService;
import com.one.frontend.util.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private static  String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private static String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private static String redirectUri;

    private final String tokenEndpoint = "https://oauth2.googleapis.com/token";

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JWTAuthResponse>> authenticate(@RequestBody LoginDto loginDto) {
        try {
            LoginResponse loginResponse = authService.login(loginDto);

            JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
            jwtAuthResponse.setAccessToken(loginResponse.getToken());

            User user = new User();
            user.setUsername(loginResponse.getUsername());
            user.setUserUid(loginResponse.getUserUid());
            jwtAuthResponse.setUser(user);

            ApiResponse<JWTAuthResponse> response = ResponseUtils.success(200, "Login successful", jwtAuthResponse);
            return ResponseEntity.ok(response);

        } catch (AllException.InvalidCredentialsException e) {
            // 返回999错误码，表示账号密码错误
            ApiResponse<JWTAuthResponse> response = ResponseUtils.failure(999, "帳號密碼錯誤", null);
            return ResponseEntity.ok(response);

        } catch (AllException.UnverifiedUserException e) {
            // 返回998错误码，表示用户没有验证
            ApiResponse<JWTAuthResponse> response = ResponseUtils.failure(998, "用戶沒有認證，請到信箱認證", null);
            return ResponseEntity.ok(response);

        } catch (AllException.BlacklistedUserException e) {
            // 返回997错误码，表示用户是黑名单
            ApiResponse<JWTAuthResponse> response = ResponseUtils.failure(997, "黑名單用戶，無法登入", null);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 处理其他异常，返回500
            ApiResponse<JWTAuthResponse> response = ResponseUtils.failure(500, "服務氣出錯", null);
            return ResponseEntity.ok(response);
        }
    }



}