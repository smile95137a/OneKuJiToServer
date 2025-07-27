package com.one.onekuji.repository;

import com.one.onekuji.model.ProductDetail;
import com.one.onekuji.request.DetailReq;
import com.one.onekuji.response.DetailRes;
import com.one.onekuji.response.ProductDetailRes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductDetailRepository {

    @Select("SELECT * FROM product_detail")
    List<DetailRes> findAll();

    @Select("SELECT * FROM product_detail WHERE product_detail_id = #{id}")
    DetailRes findById(@Param("id") Long id);

    @Select("SELECT * FROM product_detail WHERE product_detail_id = #{id}")
    ProductDetailRes findById2(@Param("id") Long id);

    @Insert("INSERT INTO product_detail (product_id, description, note, size, quantity, stock_quantity, product_name, grade, price, sliver_price, image_urls, length, width, height, specification , probability , is_prize) " +
            "VALUES (#{productId}, #{description}, #{note}, #{size}, #{quantity}, #{stockQuantity}, #{productName}, #{grade}, #{price}, #{sliverPrice}, #{imageUrls}, #{length}, #{width}, #{height}, #{specification} , #{probability} , #{isPrize})")
    @Options(useGeneratedKeys = true, keyProperty = "productDetailId")
    int insert(DetailReq productDetailReq);


    @Update("<script>" +
            "UPDATE product_detail " +
            "<set>" +
            "<if test='productId != null'>product_id = #{productId},</if>" +
            "<if test='description != null'>description = #{description},</if>" +
            "<if test='note != null'>note = #{note},</if>" +
            "<if test='size != null'>size = #{size},</if>" +
            "<if test='quantity != null'>quantity = #{quantity},</if>" +
            "<if test='stockQuantity != null'>stock_quantity = #{stockQuantity},</if>" +
            "<if test='productName != null'>product_name = #{productName},</if>" +
            "<if test='grade != null'>grade = #{grade},</if>" +
            "<if test='price != null'>price = #{price},</if>" +
            "<if test='sliverPrice != null'>sliver_price = #{sliverPrice},</if>" +
            "<if test='imageUrls != null'>image_urls = #{imageUrls},</if>" +
            "<if test='length != null'>length = #{length},</if>" +
            "<if test='width != null'>width = #{width},</if>" +
            "<if test='height != null'>height = #{height},</if>" +
            "<if test='isPrize != null'>is_prize = #{isPrize},</if>" +
            "<if test='specification != null'>specification = #{specification},</if>" +
            "<if test='probability != null'>probability = #{probability}</if>" +
            "</set>" +
            "WHERE product_detail_id = #{productDetailId}" +
            "</script>")
    int update(DetailReq productDetailReq);



    @Delete("DELETE FROM product_detail WHERE product_detail_id = #{id}")
    int delete(@Param("id") Long id);

    @Delete("DELETE FROM product_detail WHERE product_detail_id = #{productDetailId}")
    void deleteProductDetail(Integer productDetailId);
    @Select("select * from product_detail where product_id = #{productId}")
    List<ProductDetail> getProductDetailByProductId(Long productId);


    @Update("UPDATE product_detail SET " +
            "quantity = #{quantity} " +
            "WHERE product_detail_id = #{productDetailId}")
    void updateProductDetailQuantity(ProductDetail productDetail);

    @Select("select product_name from product_detail where product_id = #{productId}")
    String getProductDetailByProduct(Long productId);
    @Delete("DELETE FROM product_detail WHERE product_id = #{productId}")
    void deleteProductDetailByProductId(Integer productId);
    @Select("select * from product_detail where product_id = #{productId} and grade = 'LAST'")
    DetailRes getAllProductDetailsByProductId(Integer productId);
    @Select("select * from product_detail where product_id = #{productId}")
    ProductDetailRes getProductById(Long productId);

    @Update("UPDATE product_detail SET " +
            "probability = #{probability} , size = #{size} , sliver_price = #{sliverPrice}" +
            "WHERE product_detail_id = #{productDetailId}")
    DetailRes updateProductDTO(DetailRes byId);

    // 根據產品ID獲取所有記錄，排除指定的詳情ID列表
    @Select("<script>" +
            "SELECT * FROM product_detail WHERE product_id = #{productId} " +
            "<if test='currentDetailIds != null and currentDetailIds.size() > 0'>" +
            "AND product_detail_id NOT IN " +
            "<foreach collection='currentDetailIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</if>" +
            "</script>")
    List<DetailRes> findByProductIdExcluding(@Param("productId") Integer productId,
                                             @Param("currentDetailIds") List<Integer> currentDetailIds);

    // 根據產品ID獲取所有記錄
    @Select("SELECT * FROM product_detail WHERE product_id = #{productId}")
    List<DetailRes> findByProductId(@Param("productId") Integer productId);
}