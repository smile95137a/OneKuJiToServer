package com.one.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Schema(description = "購物車模型")
@Table(name = "cart")
public class Cart{

    @Schema(description = "購物車唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @Schema(description = "用戶 ID", example = "1")
    @Column(name = "user_id")
    private Long userId;

    @Schema(description = "創建時間", example = "2024-08-22T15:30:00")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間", example = "2024-08-22T15:30:00")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_uid")
    private String userUid;
}
