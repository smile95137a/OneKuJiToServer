package com.one.onekuji.repository;

import com.one.onekuji.model.Banner;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BannerRepository {

    @Select("SELECT * FROM banner WHERE banner_uid = #{bannerUid}")
    Banner findById(@Param("bannerId") String bannerUid);

    @Select("SELECT * , b.product_id , b.banner_image_url as imageUrls FROM banner a left join product b on a.product_id = b.product_id")
    List<Banner> findAll();

    @Insert("INSERT INTO banner (banner_uid, banner_image_urls, product_id, status, created_at, updated_at , product_type)\n" +
            "VALUES (#{bannerUid}, #{imageUrls}, #{productId}, #{status}, #{createdAt}, #{updatedAt} , #{productType})")
    @Options(useGeneratedKeys = true, keyProperty = "bannerId")
    void insert(Banner banner);

    @Update("UPDATE banner SET banner_image_urls=#{bannerImageUrl}, product_id=#{productId}, " +
            "status=#{status}, updated_at=#{updatedAt} , product_type = #{productType} WHERE banner_id=#{bannerId}")
    void update(Banner banner);

    @Delete("DELETE FROM banner WHERE banner_id = #{bannerId}")
    void deleteById(@Param("bannerId") Long bannerId);
}
