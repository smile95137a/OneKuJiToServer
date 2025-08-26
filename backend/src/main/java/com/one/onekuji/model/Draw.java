package com.one.onekuji.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Schema(description = "抽獎主表模型")
@Table(name = "draw")
public class Draw{

    @Schema(description = "抽獎唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "用戶 ID", example = "1")
    @Column(name = "user_id")
    private Long userId;

    @Schema(description = "總抽獎次數", example = "10")
    @Column(name = "total_draw_count")
    private Integer totalDrawCount;

    @Schema(description = "總金額", example = "500.00")
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Schema(description = "抽獎時間", example = "2024-08-22T15:30:00")
    @Column(name = "draw_time")
    private LocalDateTime drawTime;

    @Schema(description = "狀態", example = "ACTIVE")
    @Column(name = "status", length = 20)
    private String status;

    @Schema(description = "創建日期", example = "2024-08-22T15:30:00")
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Schema(description = "更新日期", example = "2024-08-22T15:30:00")
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
