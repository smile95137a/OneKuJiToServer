package com.one.frontend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRes {

    private Long cartItemId; // 购物车项的唯一标识符

    private String productName; // 商品名称

    private String productCode; // 商品代码

    private String description; // 商品描述

    private BigDecimal unitPrice; // 单价

    private BigDecimal totalPrice; // 总价

    private Integer quantity; // 数量

    private Boolean isSelected; // 是否选中
    
    private List<String> imageUrls; // 商品图片URL列表

    private Long size; //回傳size 可以計算體積算出運費
}
