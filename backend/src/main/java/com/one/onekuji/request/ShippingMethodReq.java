package com.one.onekuji.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShippingMethodReq {
    private Long shippingMethodId;
    private String name;

    private BigDecimal minSize;

    private BigDecimal maxSize;

    private BigDecimal shippingPrice;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
    private String code;
}
