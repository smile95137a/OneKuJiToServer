package com.one.model;

import com.one.eenum.BannerStatus;
import com.one.eenum.ProductType;
import com.one.util.StringListConverter;
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Schema(description = "產品類型", example = "GACHA")
    @Column(name = "product_type", length = 50)
    @Enumerated(EnumType.STRING)  // 新增此行
    private ProductType productType;

}
