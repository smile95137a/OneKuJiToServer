package com.one.onekuji.util;


import com.one.onekuji.model.ApiResponse;

public class ResponseUtils {

    // 成功响应
    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message != null ? message : "成功")
                .success(true)
                .data(data)
                .build();
    }

    // 失败响应
    public static <T> ApiResponse<T> failure(int code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message != null ? message : "失败")
                .success(false)
                .data(data)
                .build();
    }

    // 错误响应
    public static ApiResponse<String> error(int code, String message) {
        return ApiResponse.<String>builder()
                .code(code)
                .message(message)
                .success(false)
                .data(null)
                .build();
    }
}
