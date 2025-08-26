package com.one.onekuji.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SliverUpdate {

    private List<Long> userId;

    private BigDecimal sliverCoin;

    private BigDecimal bonus;

    private BigDecimal balance;
}
