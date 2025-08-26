package com.one.onekuji.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long id;
    private User user;
    private String type; // "TOP_UP", "PURCHASE", "BONUS_EARNED", "BONUS_USED"
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
    private Order relatedOrder; // 如果適用
}