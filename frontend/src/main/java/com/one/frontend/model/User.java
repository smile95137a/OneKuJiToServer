package com.one.frontend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "user_uid")
    private String userUid;

    @Schema(description = "用戶名稱", example = "john_doe")
    @Column(name = "username", length = 50)
    private String username;

    @Schema(description = "用戶密碼", example = "password123")
    @Column(name = "password", length = 255)
    private String password;
    @Column(name = "nat_id", length = 50)
    private String natId;

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
    @Column(name = "city", length = 255)
    private String city;
    
    @Schema(description = "用戶地址", example = "123 Main St, City, Country")
    @Column(name = "area", length = 255)
    private String area;

    @Schema(description = "用戶地址", example = "123 Main St, City, Country")
    @Column(name = "address", length = 255)
    private String address;
    
    @Schema(description = "收收貨姓名", example = "123 Main St, City, Country")
    @Column(name = "address_name", length = 255)
    private String addressName;
    
    @Column(name = "line_id", length = 255)
    private String lineId;

    @Schema(description = "用戶創建時間", example = "2024-08-22T15:30:00")
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "用戶最後更新時間", example = "2024-08-22T15:30:00")
    @Column(name = "updated_at")
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


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Set<Role> roles;

    @Column(name= "draw_count")
    private Long drawCount;
    
    @Column(name= "invoice_info")
	private String invoiceInfo;
    @Column(name= "invoice_info_email")
	private String invoiceInfoEmail;
    @Column(name="zip_code")
    private String zipCode;

    @Column(name= "vehicle")
    private String vehicle;
    
}
