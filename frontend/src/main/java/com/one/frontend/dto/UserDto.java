package com.one.frontend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

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
    private Integer roleId;

    private BigDecimal balance;
    private BigDecimal bonus;
    private BigDecimal sliverCoin;
    private String googleId;
}
