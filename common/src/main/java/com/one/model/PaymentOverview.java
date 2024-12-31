package com.one.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "支付概覽模型")
@Table(name = "payment_over_view")
@Entity
public class PaymentOverview {

    @Schema(description = "唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "用戶 ID", example = "1")
    @Column(name = "user_id")
    private Long userId;

    @Schema(description = "總金幣", example = "1000.00")
    @Column(name = "total_gold")
    private BigDecimal totalGold;

    @Schema(description = "總銀幣", example = "500.00")
    @Column(name = "total_silver")
    private BigDecimal totalSilver;

    @Schema(description = "總紅利", example = "300.00")
    @Column(name = "total_bonus")
    private BigDecimal totalBonus;

    @Schema(description = "總支出金幣", example = "200.00")
    @Column(name = "total_spent_gold")
    private BigDecimal totalSpentGold;

    @Schema(description = "總支出銀幣", example = "100.00")
    @Column(name = "total_spent_silver")
    private BigDecimal totalSpentSilver;

    @Schema(description = "總支出紅利", example = "150.00")
    @Column(name = "total_spent_bonus")
    private BigDecimal totalSpentBonus;

    @Schema(description = "最後交易日期", example = "2024-08-22T15:30:00")
    @Column(name = "last_transaction_date")
    private LocalDateTime lastTransactionDate;

    @Schema(description = "創建日期", example = "2024-08-22T15:30:00")
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Schema(description = "更新日期", example = "2024-08-22T15:30:00")
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
