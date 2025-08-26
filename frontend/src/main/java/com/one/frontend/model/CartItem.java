package com.one.frontend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
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
@Table(name = "cart_Item")
public class CartItem{

    @Schema(description = "購物車項目唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @Schema(description = "購物車 ID", example = "1")
    @Column(name = "cart_id")
    private Long cartId;

    @Schema(description = "商店產品 ID", example = "1")
    @Column(name = "store_product_id")
    private Long storeProductId;

    @Schema(description = "數量", example = "2")
    @Column(name = "quantity")
    private Integer quantity;

    @Schema(description = "單價", example = "99.99")
    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Schema(description = "總價格", example = "199.98")
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    
    @Schema(description = "是否選中", example = "true")
    @Column(name = "is_selected")
    private Boolean isSelected;

    @Schema(description = "體積大小")
    @Column(name = "size")
    private BigDecimal size;
}
