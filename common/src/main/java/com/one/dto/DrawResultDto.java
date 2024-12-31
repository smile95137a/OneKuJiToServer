package com.one.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DrawResultDto {

    private Long drawId;

    private Long userId;

    private Long productId;

    private Long productDetailId;

    private LocalDateTime drawTime;

    private BigDecimal amount;

    private Integer drawCount;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String productName;
    private List<String> imageUrls;
    private BigDecimal sliverPrice;
    private BigDecimal price;
    private BigDecimal bonusPrice;

    private String payType;

}
