package com.one.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecommendRes {
    private Long id;
    private List<String> imageUrls;
    private String recommendation_name;
    private String productName;
    private Long storeProductId;
    private Long productId;
    private String price;
    private String sliverPrice;
    private String productCode;
}
