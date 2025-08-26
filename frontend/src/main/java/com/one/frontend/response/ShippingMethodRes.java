package com.one.frontend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShippingMethodRes {

    private String shippingCode;

    private String name;

    private BigDecimal size;

    private BigDecimal shippingPrice;

    private String code;

}
