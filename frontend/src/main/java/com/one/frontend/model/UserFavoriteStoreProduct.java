package com.one.frontend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_favorite_store_products")
public class UserFavoriteStoreProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 唯一标识符

    @Column(name = "user_id")
    private Long userId; // 用户ID，关联到用户表的主键

    @Column(name = "store_product_id")
    private Long storeProductId; // 商品ID，关联到StoreProduct表的主键
}
