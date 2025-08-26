package com.one.frontend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Schema(description = "商店產品運輸方式模型")
@Table(name = "store_product_shipping_method")
@Entity
public class StoreProductShippingMethod{

    @Schema(description = "產品詳細 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipping_method_id ;

    @Schema(description = "商店產品 ID", example = "1")
    @Column(name = "store_product_id")
    private Long storeProductId;
}
