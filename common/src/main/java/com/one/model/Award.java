package com.one.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Award {
    private BigDecimal cumulative; // 累计消费金额
    private List<RewardStatus> rewardStatusList; // 每个达标金额、对应的银币数量和是否达标

}

