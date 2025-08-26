package com.one.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "marquee_detail")
public class MarqueeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "marquee_id", nullable = false)
    private Long marqueeId; // 修正命名，使用駝峰命名法以符合 Java 標準

    @Column(nullable = false, length = 100)
    private String grade; // 等級，例如 A賞、B賞

    @Column(nullable = false, length = 100)
    private String name; // 獎項名稱，例如金獎、銀獎
}
