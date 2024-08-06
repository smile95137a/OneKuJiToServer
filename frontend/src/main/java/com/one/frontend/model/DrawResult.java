package com.one.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrawResult {
    private Long id;
    private Long userId;
    private Long productId;
    private Long productDetailId;
    private String productName;
    private LocalDateTime drawTime;
    private String status;
    private BigDecimal amount;
    private Integer drawCount;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Integer totalDrawCount; //總共抽獎次數
    private Integer remainingDrawCount; // 剩餘抽獎次數
    private Integer prizeNumber;
}
