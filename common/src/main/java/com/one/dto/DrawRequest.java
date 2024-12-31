package com.one.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.one.frontend.eenum.PrizeCategory;
import com.one.frontend.eenum.ProductType;
import com.one.frontend.util.PrizeCategoryDeserializer;
import com.one.frontend.util.PrizeCategorySerializer;
import com.one.frontend.util.ProductTypeDeserializer;
import com.one.frontend.util.ProductTypeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DrawRequest {
    private Long productId;
    private Long productDetailId;
    private String productName;
    @JsonSerialize(using = ProductTypeSerializer.class)
    @JsonDeserialize(using = ProductTypeDeserializer.class)
    private ProductType productType;

    @JsonSerialize(using = PrizeCategorySerializer.class)
    @JsonDeserialize(using = PrizeCategoryDeserializer.class)
    private PrizeCategory prizeCategory;
    private BigDecimal amount;
    private Integer totalDrawCount; //總共抽獎次數
    private Integer remainingDrawCount; // 剩餘抽獎次數
}
