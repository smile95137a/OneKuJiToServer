package com.one.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductDetailRes {
    private Long productDetailId;
    private Long productId;
    private String description;
    private String note;
    private BigDecimal size;
    private Integer quantity;
    private Integer stockQuantity;
    private String productName;
    private String grade;
    private BigDecimal price;
    private BigDecimal sliverPrice;
    private List<String> imageUrls;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String specification;
    private String status;
    private String prizeNumber;
    private String drawnNumbers;
    private String isPrize;
}
