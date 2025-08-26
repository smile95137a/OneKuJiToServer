package com.one.onekuji.repository;

import com.one.onekuji.model.ProductRecommendationMapping;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductRecommendationMappingMapper {

    @Select("SELECT a.id , a.store_product_id , a.store_product_recommendation_id , a.created_date , b.product_name , c.recommendation_name , b.image_urls FROM product_recommendation_mapping a join store_product b on a.store_product_id = b.store_product_id join store_product_recommendation c on a.store_product_recommendation_id = c.id")
    List<ProductRecommendationMapping> getAllMappings();

    @Select("SELECT * FROM product_recommendation_mapping WHERE id = #{id}")
    ProductRecommendationMapping getMappingById(Long id);

    @Insert("INSERT INTO product_recommendation_mapping(store_product_id, store_product_recommendation_id   , created_date, updated_date) " +
            "VALUES (#{storeProductId}, #{storeProductRecommendationId}, #{createdDate}, #{updatedDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createMapping(ProductRecommendationMapping mapping);

    @Update("UPDATE product_recommendation_mapping SET store_product_id = #{storeProductId}, store_product_recommendation_id = #{storeProductRecommendationId}, " +
            "updated_date = #{updatedDate}, update_user = #{updateUser} WHERE id = #{id}")
    int updateMapping(ProductRecommendationMapping mapping);

    @Delete("DELETE FROM product_recommendation_mapping WHERE id = #{id}")
    int deleteMapping(Long id);

    @Delete("DELETE FROM product_recommendation_mapping WHERE store_product_id = #{storeProductId}")
    void delete(Long storeProductId);
}
