package com.one.onekuji.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User 模型，表示系統中的用戶信息")
@Table(name = "user")
public class User{

    public enum UserType {
        USER, // 正式会员
        EXP_USER // 体验会员
    }

    @Schema(description = "用戶唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "用戶名稱", example = "john_doe")
    @Column(name = "username", length = 50)
    private String username;

    @Schema(description = "用戶密碼", example = "password123")
    @Column(name = "password", length = 255)
    private String password;

    @Schema(description = "用戶暱稱", example = "John")
    @Column(name = "nickname", length = 50)
    private String nickname;

    @Schema(description = "用戶電子郵件", example = "john.doe@example.com")
    @Column(name = "email", length = 100)
    private String email;

    @Schema(description = "用戶電話號碼", example = "+123456789")
    @Column(name = "phoneNumber", length = 20)
    private String phoneNumber;

    @Schema(description = "用戶地址", example = "123 Main St, City, Country")
    @Column(name = "address", length = 255)
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    @Schema(description = "用戶創建時間", example = "2024-08-22T15:30:00")
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    @Schema(description = "用戶最後更新時間", example = "2024-08-22T15:30:00")
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Schema(description = "用戶角色識別碼", example = "1")
    @Column(name = "role_id")
    private Long roleId;

    @Schema(description = "用戶狀態", example = "ACTIVE")
    @Column(name = "status", length = 20)
    private String status;

    @Schema(description = "用戶餘額", example = "100.00")
    @Column(name = "balance", length = 10)
    private BigDecimal balance;

    @Schema(description = "用戶紅利", example = "50.00")
    @Column(name = "bonus", length = 10)
    private BigDecimal bonus;

    @Schema(description = "用戶銀幣", example = "20.00")
    @Column(name = "sliver_coin", length = 10)
    private BigDecimal sliverCoin;

    @Schema(description = "用戶提供者", example = "local")
    @Column(name = "provider", length = 100)
    private String provider;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Set<Role> roles;
}
