package com.one.onekuji.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "PrizeDetail 模型，表示獎品的詳細信息")
public class PrizeDetail {

    @Schema(description = "獎品的 ID", example = "1")
    private Long prizeId;

    @Schema(description = "獎品的描述", example = "一個超級稀有的收藏品")
    private String description;

    @Schema(description = "獎品的稀有度", example = "5")
    private int rarity;

    @Schema(description = "獎品的尺寸", example = "中等")
    private String size;

    @Schema(description = "獎品的材質", example = "塑膠")
    private String material;

    @Schema(description = "是否為秘密獎品", example = "true")
    private boolean isSecret;

    @Schema(description = "獎品的數量", example = "10")
    private int quantity;

    @Schema(description = "獎品詳細信息的 ID", example = "1")
    private Integer PrizeDetailId;

    @Schema(description = "獎品名稱", example = "金色稀有徽章")
    private String productName;

    @Schema(description = "獎品的等級", example = "A")
    private String grade;

    @Schema(description = "獎品的數量", example = "1")
    private Integer count;

    @Schema(description = "獎品詳細信息的創建日期", example = "2023-07-01T12:34:56")
    private LocalDateTime createDate;

    @Schema(description = "獎品詳細信息的更新日期", example = "2023-07-20T08:45:23")
    private LocalDateTime updateDate;

    @Schema(description = "產品的圖片 URL", example = "http://example.com/product.jpg")
    private String image;

    @Schema(description = "獎品的狀態", example = "有效")
    private String status;
}
