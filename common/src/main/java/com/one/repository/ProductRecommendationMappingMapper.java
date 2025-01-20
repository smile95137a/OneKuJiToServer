package com.one.repository;

import com.one.response.RecommendRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductRecommendationMappingMapper {

    @Select("SELECT b.image_urls , b.product_name FROM product_recommendation_mapping a left join store_product b on a.store_product_id = b.store_product_id")
    List<RecommendRes> getAllMappings();

    @Select("SELECT c.id , c.recommendation_name , b.store_product_id , b.product_code, b.image_urls , b.product_name , b.price FROM product_recommendation_mapping a left join store_product b on a.store_product_id = b.store_product_id left join store_product_recommendation c on a.store_product_recommendation_id = c.id where c.id = #{id}")
    List<RecommendRes> getMappingById(Long id);
    @Select("SELECT c.id , c.recommendation_name , b.product_id, b.image_urls , b.product_name , d.price , d.sliver_price FROM product_recommendation_mapping a left join product_detail b on a.product_detail_id = b.product_detail_id left join store_product_recommendation c on a.store_product_recommendation_id = c.id left join product d on b.product_id = d.product_id where c.id = #{id}")
    List<RecommendRes> getMappingById2(Long id);
}
