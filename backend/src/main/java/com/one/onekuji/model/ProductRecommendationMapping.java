package com.one.onekuji.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.one.onekuji.util.StringListConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "商品與推薦類別關聯模型")
@Table(name = "product_recommendation_mapping")
@Entity
public class ProductRecommendationMapping {

    @Schema(description = "關聯唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Schema(description = "商店產品 ID", example = "1")
    @Column(name = "store_product_id")
    private Long storeProductId;

    @Schema(description = "推薦類別 ID", example = "1")
    @Column(name = "store_product_recommendation_id")
    private Long storeProductRecommendationId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    @Schema(description = "創建時間", example = "2024-08-22T15:30:00")
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    @Schema(description = "更新時間", example = "2024-08-22T15:30:00")
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Schema(description = "創建用戶 ID", example = "1")
    @Column(name = "created_user")
    private String createdUser;

    @Schema(description = "更新用戶 ID", example = "1")
    @Column(name = "update_user")
    private String updateUser;

    private String productName;

    private String recommendationName;
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls;
}
