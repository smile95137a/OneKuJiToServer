package com.one.onekuji.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;

@Data
@Schema(description = "產品詳細運輸方式模型")
@Table(name = "product_detail_shipping_method")
@Entity
public class ProductDetailShippingMethod{

    @Schema(description = "產品詳細 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productDetailId;

    @Schema(description = "運輸方式 ID", example = "1")
    @Column(name = "shipping_method_id")
    private Long shippingMethodId;
}
