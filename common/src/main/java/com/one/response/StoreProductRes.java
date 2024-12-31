package com.one.response;

import com.one.model.StoreKeyword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreProductRes {

    private Long storeProductId; // 商品唯一标识符

    private String productCode; // 商品代码，唯一标识商品

    private String productName; // 商品名称

    private String description; // 商品描述，详细介绍商品信息
    
    private String details; // 商品详情，包含更详细的商品信息

    private String specification; // 商品规格，可能包含详细的技术参数或其他规格信息

    private BigDecimal price; // 商品价格

    private BigDecimal specialPrice; // 商品特价

    private Boolean isSpecialPrice; // 是否为特价商品

    private Integer stockQuantity; // 商品库存数量

    private Integer soldQuantity; // 已售出商品数量

    private List<String> imageUrls; // 商品图片URL列表

    private String categoryId; // 商品所属分类的ID
    
    private String categoryName;

    private String status; // 商品状态（例如：上架、下架）

    private String shippingMethod; // 商品运送方式

    private BigDecimal shippingPrice; // 商品运费

    private BigDecimal size; // 商品尺寸（可能是体积或其他）

    private BigDecimal length; // 商品长度

    private BigDecimal width; // 商品宽度

    private BigDecimal height; // 商品高度

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 最后更新时间

    private Long createdUserId; // 创建商品的用户ID

    private Long updateUserId; // 最后更新商品的用户ID

    private Integer popularity; // 热度（受欢迎程度）

    private int favoritesCount; // 收藏数量
    
    private Boolean favorited;
    
    private List<StoreKeyword> keywordList;
}
