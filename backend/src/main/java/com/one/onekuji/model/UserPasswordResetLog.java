package com.one.onekuji.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Schema(description = "用戶密碼重置日誌模型")
@Table(name = "user_password_reset_log")
@Entity
public class UserPasswordResetLog{

    @Schema(description = "唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "用戶 ID", example = "1")
    @Column(name = "user_id")
    private Long userId;

    @Schema(description = "重置令牌", example = "token123456")
    @Column(name = "reset_token", length = 255)
    private String resetToken;

    @Schema(description = "重置時間", example = "2024-08-22T15:30:00")
    @Column(name = "reset_time")
    private LocalDateTime resetTime;

    @Schema(description = "令牌過期時間", example = "2024-08-23T15:30:00")
    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;
}
