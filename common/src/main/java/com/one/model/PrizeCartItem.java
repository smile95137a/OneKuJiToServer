package com.one.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Schema(description = "購物車項目模型")
@Table(name = "prize_cart_item")
public class PrizeCartItem{

    @Schema(description = "購物車項目唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prizeCartItemId;

    @Schema(description = "購物車 ID", example = "1")
    @Column(name = "cart_id")
    private Long cartId;

    @Schema(description = "商店產品 ID", example = "1")
    @Column(name = "product_detail_id")
    private Long productDetailId;

    @Schema(description = "數量", example = "2")
    @Column(name = "quantity")
    private Integer quantity;

    @Schema(description = "單價", example = "99.99")
    @Column(name = "sliver_price")
    private BigDecimal sliverPrice;

    @Schema(description = "是否選中", example = "true")
    @Column(name = "is_selected")
    private Boolean isSelected;

    @Schema(description = "體積大小")
    @Column(name = "size")
    private BigDecimal size;

}
