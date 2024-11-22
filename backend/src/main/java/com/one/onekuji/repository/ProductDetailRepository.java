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


    @Update("UPDATE product_detail SET " +
            "product_id = #{productId}, " +
            "description = #{description}, " +
            "note = #{note}, " +
            "size = #{size}, " +
            "quantity = #{quantity}, " +
            "stock_quantity = #{stockQuantity}, " +
            "product_name = #{productName}, " +
            "grade = #{grade}, " +
            "price = #{price}, " +
            "sliver_price = #{sliverPrice}, " +
            "image_urls = #{imageUrls}, " +
            "length = #{length}, " +
            "width = #{width}, " +
            "height = #{height}, " +
            "is_prize = #{isPrize}, " +
            "specification = #{specification}, " +  // 这里加上逗号
            "probability = #{probability} " +
            "WHERE product_detail_id = #{productDetailId}")
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
}