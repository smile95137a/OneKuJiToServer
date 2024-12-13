package com.one.onekuji.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRes {

    private Long id;

    private String nickName;

    @Schema(description = "用戶名", example = "john_doe")
    private String username;

    @Schema(description = "用戶密碼", example = "password123")
    private String password;

    @Schema(description = "用戶的電子郵件地址", example = "john.doe@example.com")
    private String email;

    @Schema(description = "用戶的電話號碼", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "用戶的地址", example = "123 Main Street, City, Country")
    private String address;

    @Schema(description = "用户角色 ID", example = "2")
    private Long roleId;

    private BigDecimal balance;
    private BigDecimal bonus;
    private BigDecimal sliverCoin;
    private String googleId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    private LocalDateTime updatedAt;
    private Long drawCount;
    private String addressName;
    private String city;
    private String area;
    private String provider;
}
