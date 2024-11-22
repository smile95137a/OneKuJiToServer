package com.one.onekuji.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailReq {

    private Integer productDetailId;
    private Integer productId;
    private String description;
    private String note;
    private String size;
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
    private Double probability;
    private String isPrize;
}
