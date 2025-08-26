package com.one.onekuji.request;

import com.one.onekuji.eenum.StoreProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreProductReq {
    private Long storeProductId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private List<String> imageUrl;
    private String categoryId;
    private StoreProductStatus status;
    private BigDecimal specialPrice;
    private String shippingMethod;
    private BigDecimal size;
    private BigDecimal shippingPrice;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String specification;
    private String productCode;
    private String details; // 商品详情，包含更详细的商品信息
}
