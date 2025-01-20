package com.one.onekuji.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDTO {
    private BigDecimal sliverPrice;

    private Double probability;

    private String size;

}
