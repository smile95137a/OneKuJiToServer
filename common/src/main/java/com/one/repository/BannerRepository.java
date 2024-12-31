package com.one.repository;

import com.one.frontend.model.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BannerRepository {

    @Select("SELECT * FROM banner WHERE banner_id = #{bannerId}")
    Banner findById(@Param("bannerId") Long bannerId);

    @Select("SELECT * , b.product_id , b.banner_image_url as imageUrls , b.product_type FROM banner a left join product b on a.product_id = b.product_id")
    List<Banner> findAll();

}
