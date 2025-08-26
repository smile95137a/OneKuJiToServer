package com.one.onekuji.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Schema(description = "商店產品運輸方式模型")
@Table(name = "store_product_shipping_method")
@Entity
public class StoreProductShippingMethod {

    @Schema(description = "產品詳細 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_detail_id")
    private Long productDetailId;

    @ManyToOne
    @JoinColumn(name = "store_product_id", nullable = false)
    private StoreProduct storeProduct;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id", nullable = false)
    private ShippingMethod shippingMethod;
}
