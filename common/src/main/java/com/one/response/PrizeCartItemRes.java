package com.one.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrizeCartItemRes {

    private Long prizeCartItemId;

    private Long cartId;

    private Long productDetailId;

    private Integer quantity;

    private BigDecimal sliverPrice;

    private Boolean isSelected;

    private BigDecimal size;

    private List<String> imageUrls; // 商品图片URL列表

    private String productName; // 商品名称
}
