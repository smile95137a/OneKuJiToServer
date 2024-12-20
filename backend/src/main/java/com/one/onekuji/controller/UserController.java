package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.User;
import com.one.onekuji.request.SliverUpdate;
import com.one.onekuji.request.UserReq;
import com.one.onekuji.response.UserRes;
import com.one.onekuji.service.UserService;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 查詢所有用戶
    @GetMapping("/query")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserRes>>> getAllUsers() {
        List<UserRes> userList = userService.getAllUsers();
        if (userList == null || userList.isEmpty()) {
            ApiResponse<List<UserRes>> response = ResponseUtils.failure(404, "無用戶", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<List<UserRes>> response = ResponseUtils.success(200, null, userList);
        return ResponseEntity.ok(response);
    }

    // 根據ID查詢用戶
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserRes>> getUserById(@PathVariable("userId") Long userId) {
        UserRes user = userService.getUserById(userId);
        if (user == null) {
            ApiResponse<UserRes> response = ResponseUtils.failure(404, "用戶未找到", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<UserRes> response = ResponseUtils.success(200, null, user);
        return ResponseEntity.ok(response);
    }

    // 新增用戶
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody UserReq userReq) {
        User res = userService.createUser(userReq);
        ApiResponse<User> response = ResponseUtils.success(201, "用戶創建成功", res);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 更新用戶
    @PutMapping("/update/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable("userId") Long userId, @RequestBody UserReq userReq) {
        try {
            User res = userService.updateUser(userId, userReq);
            ApiResponse<User> response = ResponseUtils.success(200, "用戶更新成功", res);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ApiResponse<User> response = ResponseUtils.success(500, "錯誤", null);
        return ResponseEntity.ok(response);
    }

    // 刪除用戶
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        ApiResponse<Void> response = ResponseUtils.success(200, "用戶刪除成功", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateSliver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateSliver(@RequestBody SliverUpdate sliverUpdate) {
        userService.updateSliver(sliverUpdate);
        ApiResponse<Void> response = ResponseUtils.success(200, "新增銀幣成功", null);
        return ResponseEntity.ok(response);
    }

}
