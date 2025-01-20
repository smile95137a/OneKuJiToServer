package com.one.onekuji.controller;

import com.one.onekuji.dto.JWTAuthResponse;
import com.one.onekuji.dto.LoginDto;
import com.one.onekuji.dto.LoginResponse;
import com.one.onekuji.exception.AllException;
import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.User;
import com.one.onekuji.service.AuthService;
import com.one.onekuji.util.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JWTAuthResponse>> authenticate(@RequestBody LoginDto loginDto) throws Exception {
        try {
            LoginResponse loginResponse = authService.login(loginDto);

            JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
            jwtAuthResponse.setAccessToken(loginResponse.getToken());

            User user = new User();
            user.setId(loginResponse.getId());
            user.setUsername(loginResponse.getUsername());
            user.setRoleId(Long.valueOf(loginResponse.getRoleId()));
            jwtAuthResponse.setUser(user);
            ApiResponse<JWTAuthResponse> response = ResponseUtils.success(200, null, jwtAuthResponse);

            return ResponseEntity.ok(response);
        }catch (AllException.InvalidCredentialsException e) {
        // 返回999错误码，表示账号密码错误
        ApiResponse<JWTAuthResponse> response = ResponseUtils.failure(999, "帳號密碼錯誤", null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

    } catch (AllException.UnverifiedUserException e) {
        // 返回998错误码，表示用户没有验证
        ApiResponse<JWTAuthResponse> response = ResponseUtils.failure(998, "用戶沒有認證，請到信箱認證", null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

    } catch (AllException.BlacklistedUserException e) {
        // 返回997错误码，表示用户是黑名单
        ApiResponse<JWTAuthResponse> response = ResponseUtils.failure(997, "黑名單用戶，無法登入", null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

    } catch (Exception e) {
        // 处理其他异常，返回500
        ApiResponse<JWTAuthResponse> response = ResponseUtils.failure(500, "服務氣出錯", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    }
}