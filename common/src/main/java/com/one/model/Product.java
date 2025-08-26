package com.one.model;

import com.one.eenum.PrizeCategory;
import com.one.eenum.ProductStatus;
import com.one.eenum.ProductType;
import com.one.util.StringListConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "產品模型")
@Table(name = "product")
@Entity
public class Product {

    @Schema(description = "產品唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Schema(description = "產品名稱", example = "Product Name")
    @Column(name = "product_name", length = 100)
    private String productName;

    @Schema(description = "描述", example = "This is a product description.")
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Schema(description = "價格", example = "199.99")
    @Column(name = "price")
    private BigDecimal price;

    @Schema(description = "銀幣價格", example = "100.00")
    @Column(name = "sliver_price")
    private BigDecimal sliverPrice;

    @Schema(description = "庫存數量", example = "500")
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Schema(description = "圖片 URL", example = "http://example.com/product_image.jpg")
    @Column(name = "image_urls", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls;

    @Schema(description = "稀有度", example = "Rare")
    @Column(name = "rarity", length = 50)
    private String rarity;

    @Schema(description = "創建日期", example = "2024-08-22T15:30:00")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Schema(description = "開始日期", example = "2024-08-01")
    @Column(name = "start_date")
    private LocalDate startDate;

    @Schema(description = "結束日期", example = "2024-08-31")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Schema(description = "創建用戶", example = "1")
    @Column(name = "created_user")
    private Integer createdUser;

    @Schema(description = "更新用戶", example = "2")
    @Column(name = "update_user")
    private Integer updateUser;

    @Schema(description = "更新日期", example = "2024-08-22T15:30:00")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "產品類型", example = "GACHA")
    @Column(name = "product_type", length = 50)
    @Enumerated(EnumType.STRING)  // 新增此行
    private ProductType productType;

    @Schema(description = "獎品類別", example = "Bonus")
    @Column(name = "prize_category", length = 50)
    @Enumerated(EnumType.STRING)  // 新增此行
    private PrizeCategory prizeCategory;

    @Schema(description = "狀態", example = "AVAILABLE")
    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)  // 新增此行
    private ProductStatus status;

    @Column(name = "bonus_price")
    private BigDecimal bonusPrice;

    @Column(name= "specification")
    private String specification;

    @Column(name = "length")
    private BigDecimal length;

    @Column(name = "width")
    private BigDecimal width;

    @Column(name = "height")
    private BigDecimal height;

    @Column(name = "size")
    private BigDecimal size;
}
