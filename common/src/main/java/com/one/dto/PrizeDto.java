package com.one.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrizeDto {

    @Schema(description = "獎品價格", example = "29.99")
    private BigDecimal price;

    @Schema(description = "獎品總數量", example = "1000")
    private int totalQuantity;

    @Schema(description = "獎品剩餘數量", example = "500")
    private int remainingQuantity;

    @Schema(description = "獎品主圖的 URL", example = "http://example.com/image.jpg")
    private String mainImageUrl;

    @Schema(description = "獎品發布日期", example = "2024-01-01")
    private LocalDate releaseDate;

    @Schema(description = "製造商", example = "頂級製造商有限公司")
    private String manufacturer;

    @Schema(description = "是否為限量版", example = "true")
    private boolean isLimited;

    @Schema(description = "獎品唯一標識符", example = "123")
    private Integer prizeId;

    @Schema(description = "獎品名稱", example = "超級小工具")
    private String name;

    @Schema(description = "獎品可用的開始日期", example = "2023-09-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "獎品可用的結束日期", example = "2024-01-01T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "描述", example = "這是一個很棒的獎品")
    private String description;

    @Schema(description = "未出貨數量" , example = "0")
    private Integer unshippedQuantity;


}
