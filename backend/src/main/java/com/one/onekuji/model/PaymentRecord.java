package com.one.onekuji.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "支付記錄模型")
@Table(name = "payment_record")
@Entity
public class PaymentRecord{

    @Schema(description = "唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "用戶 ID", example = "1")
    @Column(name = "user_id")
    private Long userId;

    @Schema(description = "交易 ID", example = "txn123456")
    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Schema(description = "金額", example = "50.00")
    @Column(name = "amount")
    private BigDecimal amount;

    @Schema(description = "貨幣類型", example = "GOLD")
    @Column(name = "currency_type", length = 10)
    private String currencyType;

    @Schema(description = "支付方式", example = "CREDIT_CARD")
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Schema(description = "狀態", example = "SUCCESS")
    @Column(name = "status", length = 50)
    private String status;

    @Schema(description = "交易日期", example = "2024-08-22T15:30:00")
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Schema(description = "創建日期", example = "2024-08-22T15:30:00")
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Schema(description = "更新日期", example = "2024-08-22T15:30:00")
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
