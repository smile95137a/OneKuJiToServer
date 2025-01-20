package com.one.onekuji.dto;

import com.one.onekuji.eenum.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private BigDecimal price;

    private BigDecimal sliverPrice;

    private ProductStatus status;
}
