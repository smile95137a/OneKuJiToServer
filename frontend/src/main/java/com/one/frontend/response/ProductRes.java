    package com.one.frontend.response;

    import com.one.frontend.eenum.PrizeCategory;
    import com.one.frontend.eenum.ProductStatus;
    import com.one.frontend.eenum.ProductType;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.math.BigDecimal;
    import java.util.List;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ProductRes {
        private Integer productId;
        private String productName;
        private String description;
        private BigDecimal price;
        private BigDecimal sliverPrice;
        private Integer stockQuantity;
        private List<String> imageUrls;
        private ProductType productType;
        private PrizeCategory prizeCategory;
        private ProductStatus status;
        private BigDecimal bonusPrice;
        private BigDecimal length;
        private BigDecimal width;
        private BigDecimal height;
        private String specification;
        private BigDecimal size;

        private Integer detailQuantity;
        private Integer detailStockQuantity;
        private String categoryUUid;
        private List<ProductDetailRes> productDetails;
    }
