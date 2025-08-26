package com.one.onekuji.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;
@Data
@Schema(description = "獎品編號模型")
@Table(name = "prize_number")
@Entity
public class PrizeNumber{

    @Schema(description = "獎品編號唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prizeNumberId;

    @Schema(description = "產品 ID", example = "1")
    @Column(name = "product_id")
    private Integer productId;

    @Schema(description = "產品詳細 ID", example = "1")
    @Column(name = "product_detail_id")
    private Integer productDetailId;

    @Schema(description = "獎品編號", example = "PRIZE001")
    @Column(name = "number", length = 50)
    private String number;

    @Schema(description = "是否已抽中", example = "true")
    @Column(name = "is_drawn")
    private Boolean isDrawn;

    @Schema(description = "獎品級別", example = "A")
    @Column(name = "level", length = 50)
    private String level;

    @Column(name = "probability")
    private Double probability;
}
