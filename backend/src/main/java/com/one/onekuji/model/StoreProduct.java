package com.one.onekuji.model;

import com.one.onekuji.util.StringListConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "商店產品模型")
@Table(name = "store_product")
@Entity
public class StoreProduct {

    @Schema(description = "商店產品唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_product_id")
    private Long storeProductId;

    @Schema(description = "產品名稱", example = "Product A")
    @Column(name = "product_name", length = 255)
    private String productName;

    @Schema(description = "描述", example = "Detailed description of Product A.")
    @Column(name = "description")
    private String description;

    @Schema(description = "價格", example = "99.99")
    @Column(name = "price")
    private BigDecimal price;

    @Schema(description = "庫存數量", example = "100")
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Schema(description = "已售數量", example = "25")
    @Column(name = "sold_quantity")
    private Integer soldQuantity;

    @Schema(description = "圖片 URL", example = "http://example.com/image.jpg")
    @Column(name = "image_urls", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls;

    @Schema(description = "類別", example = "Electronics")
    @Column(name = "category_id", length = 100)
    private String categoryId;

    @Schema(description = "狀態", example = "ACTIVE")
    @Column(name = "status", length = 50)
    private String status;

    @Schema(description = "創建時間", example = "2024-08-22T15:30:00")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間", example = "2024-08-22T15:30:00")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "創建用戶 ID", example = "1")
    @Column(name = "created_user")
    private Long createdUser;
    @Column(name = "product_code")
    private String productCode; // 商品代码，唯一标识商品
    @Schema(description = "更新用戶 ID", example = "1")
    @Column(name = "update_user")
    private Long updateUser;

    @Schema(description = "特價", example = "89.99")
    @Column(name = "special_price")
    private BigDecimal specialPrice;
    @Column(name = "shipping_method")
    private String shippingMethod;
    @Column(name = "size")
    private BigDecimal size;

    @Column(name = "length")
    private BigDecimal length;

    @Column(name = "width")
    private BigDecimal width;

    @Column(name = "height")
    private BigDecimal height;


    @Column(name = "shipping_price")
    private BigDecimal shippingPrice;

    @Column(name = "details", columnDefinition = "LONGTEXT")
    @Lob
    private String details; // 商品详情，包含更详细的商品信息

    @Column(name= "specification")
    private String specification;
}
