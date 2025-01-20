package com.one.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "訂單日誌模型")
@Table(name = "order_log")
@Entity
public class OrderLog{

    @Schema(description = "訂單日誌唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "訂單 ID", example = "1")
    @Column(name = "order_id")
    private Long orderId;

    @Schema(description = "舊狀態", example = "PENDING")
    @Column(name = "old_status", length = 50)
    private String oldStatus;

    @Schema(description = "新狀態", example = "SHIPPED")
    @Column(name = "new_status", length = 50)
    private String newStatus;

    @Schema(description = "狀態變更時間", example = "2024-08-22T16:00:00")
    @Column(name = "change_time")
    private LocalDateTime changeTime;
}
