package com.one.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Schema(description = "推薦類別模型")
@Table(name = "store_product_recommendation")
@Entity
public class StoreProductRecommendation {

    @Schema(description = "推薦類別唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Schema(description = "推薦名稱", example = "你可能會喜歡")
    @Column(name = "recommendation_name", length = 100)
    private String recommendationName;

    @Schema(description = "創建時間", example = "2024-08-22T15:30:00")
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Schema(description = "更新時間", example = "2024-08-22T15:30:00")
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Schema(description = "創建用戶 ID", example = "1")
    @Column(name = "created_user")
    private String createdUser;

    @Schema(description = "更新用戶 ID", example = "1")
    @Column(name = "update_user")
    private String updateUser;


}
