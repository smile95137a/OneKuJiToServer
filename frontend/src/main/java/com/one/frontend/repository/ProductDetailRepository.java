package com.one.frontend.repository;

import com.one.frontend.model.ProductDetail;
import com.one.frontend.response.ProductDetailRes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductDetailRepository {

    @Select("select * from product_detail")
    List<ProductDetailRes> getAllProductDetail();
    @Select("select * from product_detail where product_detail_id = #{productDetailId}")
    ProductDetailRes getProductDetailById(Long productDetailId);

    @Insert("INSERT INTO product_detail (" +
            "product_id, product_name, description, size, grade, image, create_date) " +
            "VALUES (#{productId}, #{productName}, #{description}, #{size}, #{grade}, #{image}, #{createDate})")
    @Options(useGeneratedKeys = true, keyProperty = "productDetailId")
    void createProductDetail(ProductDetail productDetail);

    @Update("UPDATE product_detail SET " +
            "product_id = #{productId}, " +
            "product_name = #{productName}, " +
            "description = #{description}, " +
            "size = #{size}, " +
            "grade = #{grade}, " +
            "image = #{image}, " +
            "update_date = #{updateDate} " +
            "WHERE product_detail_id = #{productDetailId}")
    void updateProductDetail(ProductDetail productDetail);

    @Delete("DELETE FROM product_detail WHERE product_detail_id = #{productDetailId}")
    void deleteProductDetail(Integer productDetailId);
    @Select("select * from product_detail where product_id = #{productId}")
    List<ProductDetailRes> getProductDetailByProductId(Long productId);


    @Update("UPDATE product_detail SET " +
            "quantity = #{quantity} " +
            "WHERE product_detail_id = #{productDetailId}")
    void updateProductDetailQuantity(ProductDetailRes productDetail);

    @Select("select product_name from product_detail where product_id = #{productId}")
    String getProductDetailByProduct(Long productId);

    // 获取指定产品的所有奖品详情
    @Select("SELECT * FROM product_detail WHERE product_id = #{productId}")
    List<ProductDetail> getAllProductDetailsByProductId(Long productId);

    // 根据产品ID和奖品编号获取奖品详情
    @Select("SELECT * FROM product_detail WHERE product_id = #{productId} AND prize_number = #{prizeNumber} AND quantity > 0")
    ProductDetailRes getProductDetailByNumber(@Param("productId") Long productId, @Param("prizeNumber") Integer prizeNumber);

    // 更新奖品数量和已抽中的奖品编号
    @Update("UPDATE product_detail SET quantity = #{quantity}, drawn_numbers = #{drawnNumbers} WHERE product_detail_id = #{productDetailId}")
    void updateProductDetailQuantityAndDrawnNumbers(ProductDetail productDetail);

    @Select("SELECT * FROM product_detail WHERE product_id = #{productId} AND prize_type = #{prizeType} LIMIT 1")
    ProductDetailRes findFirstByProductIdAndPrizeType(@Param("productId") Long productId, @Param("prizeType") String prizeType);

    @Select("select * from product_detail WHERE product_id = #{productId} and grade = 'LAST'")
    ProductDetailRes getProductDetailSpPrizeByProductId(Long productId);
    @Update({
            "<script>",
            "UPDATE product_detail",
            "SET quantity = CASE product_detail_id",
            "<foreach collection='list' item='detail' index='index'>",
            "WHEN #{detail.productDetailId} THEN #{detail.quantity} ",
            "</foreach>",
            "ELSE quantity END,",
            "drawn_numbers = CASE product_detail_id",
            "<foreach collection='list' item='detail' index='index'>",
            "WHEN #{detail.productDetailId} THEN #{detail.drawnNumbers} ",
            "</foreach>",
            "ELSE drawn_numbers END",
            "WHERE product_detail_id IN",
            "<foreach collection='list' item='detail' index='index' open='(' separator=',' close=')'>",
            "#{detail.productDetailId}",
            "</foreach>",
            "</script>"
    })
    void updateProductDetailQuantityAndDrawnNumbersBatch(@Param("list") List<ProductDetail> productDetails);

    @Update("update product_detail set is_prize = 'false'")
    void updateIsPrize();
}