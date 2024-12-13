package com.one.onekuji.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.one.onekuji.eenum.BannerStatus;
import com.one.onekuji.eenum.ProductType;
import com.one.onekuji.util.StringListConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private Long bannerId;

    @Column(name = "banner_uid")
    private String bannerUid;

    @Column(name = "banner_image_urls", nullable = false)
    @Schema(description = "圖片 URL", example = "http://example.com/image.jpg")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls;

    @Column(name = "product_id")
    private Long productId;

    @Enumerated(EnumType.STRING) // 将枚举映射为其名称，例如存储 'AVAILABLE'
    @Column(name = "status", nullable = false)
    private BannerStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "產品類型", example = "GACHA")
    @Column(name = "product_type", length = 50)
    @Enumerated(EnumType.STRING)  // 新增此行
    private ProductType productType;

}
