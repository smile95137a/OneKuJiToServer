package com.one.onekuji.repository;

import com.one.onekuji.model.StoreProductRecommendation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductRecommendationMapper {

    // 查詢所有推薦類別
    @Select("SELECT * FROM store_product_recommendation")
    List<StoreProductRecommendation> getAllRecommendations();

    // 根據 ID 查詢推薦類別
    @Select("SELECT * FROM store_product_recommendation WHERE id = #{id}")
    StoreProductRecommendation getRecommendationById(Long id);

    // 插入新的推薦類別
    @Insert("INSERT INTO store_product_recommendation (recommendation_name, created_date, updated_date, created_user, update_user) " +
            "VALUES (#{recommendationName}, #{createdDate}, #{updatedDate}, #{createdUser}, #{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRecommendation(StoreProductRecommendation recommendation);

    // 更新推薦類別
    @Update("UPDATE store_product_recommendation SET recommendation_name = #{recommendationName}, " +
            "updated_date = #{updatedDate}, update_user = #{updateUser} WHERE id = #{id}")
    void updateRecommendation(StoreProductRecommendation recommendation);

    // 刪除推薦類別
    @Delete("DELETE FROM store_product_recommendation WHERE id = #{id}")
    void deleteRecommendation(Long id);
}
