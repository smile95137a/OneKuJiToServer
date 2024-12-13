package com.one.onekuji.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Schema(description = "抽獎結果模型")
@Table(name = "draw_result")
public class DrawResult{

    @Schema(description = "抽獎結果唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "抽獎 ID", example = "1")
    @Column(name = "draw_id")
    private Long drawId;

    @Schema(description = "產品 ID", example = "1")
    @Column(name = "product_id")
    private Long productId;

    @Schema(description = "產品詳細 ID", example = "1")
    @Column(name = "product_detail_id")
    private Long productDetailId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    @Schema(description = "抽獎時間", example = "2024-08-22T15:30:00")
    @Column(name = "draw_time")
    private LocalDateTime drawTime;

    @Schema(description = "抽獎金額", example = "100.00")
    @Column(name = "amount")
    private BigDecimal amount;

    @Schema(description = "抽獎次數", example = "5")
    @Column(name = "draw_count")
    private Integer drawCount;

    @Schema(description = "剩餘抽獎次數", example = "2")
    @Column(name = "remaining_draw_count")
    private Integer remainingDrawCount;

    @Schema(description = "獎品編號", example = "PRIZE001")
    @Column(name = "prize_number", length = 50)
    private String prizeNumber;

    @Schema(description = "狀態", example = "ACTIVE")
    @Column(name = "status", length = 20)
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    @Schema(description = "創建日期", example = "2024-08-22T15:30:00")
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    @Schema(description = "更新日期", example = "2024-08-22T15:30:00")
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
