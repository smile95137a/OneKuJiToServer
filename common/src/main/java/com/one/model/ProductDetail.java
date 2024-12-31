package com.one.model;

import com.one.frontend.util.StringListConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "產品詳細模型")
@Table(name = "product_detail")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {

    @Schema(description = "產品詳細唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productDetailId;

    @Schema(description = "產品 ID", example = "1")
    @Column(name = "product_id")
    private Long productId;

    @Schema(description = "描述", example = "This is a detailed description.")
    @Column(name = "description", length = 255)
    private String description;

    @Schema(description = "備註", example = "No special notes.")
    @Column(name = "note", length = 255)
    private String note;

    @Schema(description = "稀有度", example = "Rare")
    @Column(name = "rarity", length = 50)
    private String rarity;

    @Schema(description = "尺寸", example = "Medium")
    @Column(name = "size", length = 50)
    private String size;

    @Schema(description = "材質", example = "Plastic")
    @Column(name = "material", length = 50)
    private String material;

    @Schema(description = "數量", example = "100")
    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Schema(description = "產品名稱", example = "Product Name")
    @Column(name = "product_name", length = 100)
    private String productName;

    @Schema(description = "等級", example = "A")
    @Column(name = "grade", length = 50)
    private String grade;

    @Schema(description = "價格", example = "199.99")
    @Column(name = "price")
    private BigDecimal price;

    @Schema(description = "銀幣價格", example = "100.00")
    @Column(name = "sliver_price")
    private BigDecimal sliverPrice;

    @Schema(description = "創建日期", example = "2024-08-22T15:30:00")
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Schema(description = "更新日期", example = "2024-08-22T15:30:00")
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Schema(description = "圖片 URL", example = "http://example.com/image.jpg")
    @Column(name = "image_urls", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls;

    @Schema(description = "獎品編號", example = "PRIZE001")
    @Column(name = "prize_number", length = 50)
    private String prizeNumber;

    @Schema(description = "抽中號碼", example = "PRIZE001,PRIZE002")
    @Column(name = "drawn_numbers", length = 255)
    private String drawnNumbers;

    @Column(name= "specification")
    private String specification;

    @Column(name = "length")
    private BigDecimal length;

    @Column(name = "width")
    private BigDecimal width;

    @Column(name = "height")
    private BigDecimal height;


    public ProductDetail(Long productDetailId, int newQuantity, String drawnNumbers) {
        this.productDetailId = productDetailId;
        this.quantity = newQuantity;
        this.drawnNumbers = drawnNumbers;
    }
}
