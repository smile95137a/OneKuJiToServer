package com.one.onekuji.request;

import com.one.onekuji.eenum.PrizeCategory;
import com.one.onekuji.eenum.ProductStatus;
import com.one.onekuji.eenum.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductReq {

    private Long productId;
    private String productName;
    private String description;
    private Long price;
    private BigDecimal sliverPrice;
    private Integer stockQuantity;
    private List<String> imageUrls;
    private ProductType productType;
    private PrizeCategory prizeCategory;
    private ProductStatus status;
    private BigDecimal bonusPrice;
    private String specification;
    private Long categoryId;
    private List<String> bannerImageUrl;
}
