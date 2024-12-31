package com.one.filter;

import com.one.frontend.model.ApiResponse;
import com.one.frontend.util.ResponseUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.MethodNotAllowedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResponse<String> handleException(Exception e) {
        return ResponseUtils.error(500, e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ApiResponse<String> handleNullPointerException(NullPointerException e) {
        return ResponseUtils.error(500, "空指針: " + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseUtils.error(400, "非法参数: " + e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseBody
    public ApiResponse<String> handleDataAccessException(DataAccessException e) {
        return ResponseUtils.error(500, "數據庫訪問錯誤: " + e.getMessage());
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    @ResponseBody
    public ApiResponse<String> handleMethodNotAllowedException(MethodNotAllowedException e) {
        return ResponseUtils.error(405, "方法不允许: " + e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public ApiResponse<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseUtils.error(500, "非法狀態: " + e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseUtils.error(400, "驗證錯誤: " + e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ApiResponse<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ResponseUtils.error(415, "不支援此媒體類型: " + e.getMessage());
    }

    // 可以添加更多的@ExceptionHandler來處理不同類型的異常
}
