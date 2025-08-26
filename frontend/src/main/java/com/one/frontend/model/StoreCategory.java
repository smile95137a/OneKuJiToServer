package com.one.frontend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Schema(description = "商店類別模型")
@Table(name = "store_category")
@Entity
public class StoreCategory{

    @Schema(description = "類別唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Schema(description = "類別名稱", example = "Electronics")
    @Column(name = "category_name", length = 100)
    private String categoryName;
}
