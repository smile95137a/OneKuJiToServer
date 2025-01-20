package com.one.model;

import com.one.eenum.NewsStatus;
import com.one.util.StringListConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 自增主键
    private Long id;

    @Column(name = "news_uid")
    private String newsUid;

    @Column(name = "title", nullable = false, length = 255) // 新闻标题，不能为空，最大长度 255
    private String title;

    @Column(name = "preview", length = 500) // 新闻预览文字，最大长度 500
    private String preview;

    @Column(name = "content", columnDefinition = "TEXT") // 新闻详细内容，使用 TEXT 类型以支持较大文本
    private String content;

    @Schema(description = "圖片 URL", example = "http://example.com/image.jpg")
    @Column(name = "image_urls", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING) // 将枚举映射为其名称，例如存储 'AVAILABLE'
    @Column(name = "status", nullable = false)
    private NewsStatus status;
    @Column(name = "created_date", nullable = false) // 创建时间，不能为空
    private LocalDateTime createdDate;

    @Column(name = "updated_date") // 最后更新时间，可为空
    private LocalDateTime updatedDate;

    @Column(name = "author", length = 100) // 作者信息，最大长度 100
    private String author;

}
