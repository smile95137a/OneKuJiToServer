package com.one.onekuji.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "ranking_list")
public class RankingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "product_id", nullable = false, unique = true)
    private String productId;

    @Column(name = "product_count", nullable = false)
    private Integer productCount;

    @Column(name = "status")
    private String status;

}
