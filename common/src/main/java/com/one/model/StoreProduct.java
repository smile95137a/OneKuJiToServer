package com.one.model;

import com.one.frontend.util.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "store_product")
@Entity
public class StoreProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_product_id")
    private Long storeProductId; // 商品唯一标识符

    @Column(name = "product_code")
    private String productCode; // 商品代码，唯一标识商品

    @Column(name = "product_name", length = 255)
    private String productName; // 商品名称

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description; // 商品描述，详细介绍商品信息
    
    @Column(name = "details", columnDefinition = "LONGTEXT")
    @Lob
    private String details; // 商品详情，包含更详细的商品信息

    @Column(name = "specification", columnDefinition = "LONGTEXT")
    @Lob
    private String specification; // 商品规格，可能包含详细的技术参数或其他规格信息

    @Column(name = "price")
    private BigDecimal price; // 商品价格

    @Column(name = "special_price")
    private BigDecimal specialPrice; // 商品特价

    @Column(name = "is_special_price")
    private Boolean isSpecialPrice; // 是否为特价商品

    @Column(name = "stock_quantity")
    private Integer stockQuantity; // 商品库存数量

    @Column(name = "sold_quantity")
    private Integer soldQuantity; // 已售出商品数量

    @Column(name = "image_urls", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls; // 商品图片URL列表，存储为JSON格式

    @Column(name = "category_id", length = 100)
    private String categoryId; // 商品所属分类的ID

    @Column(name = "status", length = 50)
    private String status; // 商品状态（例如：上架、下架）

    @Column(name = "shipping_method")
    private String shippingMethod; // 商品运送方式

    @Column(name = "shipping_price")
    private BigDecimal shippingPrice; // 商品运费

    @Column(name = "size")
    private BigDecimal size; // 商品尺寸（可能是体积或其他）

    @Column(name = "length")
    private BigDecimal length; // 商品长度

    @Column(name = "width")
    private BigDecimal width; // 商品宽度

    @Column(name = "height")
    private BigDecimal height; // 商品高度

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 最后更新时间

    @Column(name = "created_user_id")
    private Long createdUserId; // 创建商品的用户ID

    @Column(name = "update_user_id")
    private Long updateUserId; // 最后更新商品的用户ID
    
    @Column(name = "popularity")
    private Integer popularity; // 热度（受欢迎程度）
}
