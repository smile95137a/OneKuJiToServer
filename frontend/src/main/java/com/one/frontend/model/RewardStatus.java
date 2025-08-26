package com.one.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardStatus {
    private BigDecimal threshold; // 达标金额
    private int sliver;           // 对应银币数量
    private boolean achieved;     // 是否达标

}
