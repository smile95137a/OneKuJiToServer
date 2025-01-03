package com.one.frontend.repository;

import com.one.frontend.model.Product;
import com.one.frontend.response.ProductDetailRes;
import com.one.frontend.response.ProductRes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductRepository {

    @Select("WITH product_summary AS (" +
            "  SELECT product_id, " +
            "    SUM(quantity) as detailQuantity, " +
            "    SUM(stock_quantity) as detailStockQuantity " +
            "  FROM product_detail " +
            "  WHERE grade <> 'LAST' " + // 移到 WITH 子句中，減少需要處理的數據量
            "  GROUP BY product_id" +
            ") " +
            "SELECT p.*, " +
            "       pc.category_uuid, " +
            "       COALESCE(ps.detailQuantity, 0) as detailQuantity, " + // 使用 COALESCE 處理 NULL
            "       COALESCE(ps.detailStockQuantity, 0) as detailStockQuantity " +
            "FROM product p " +
            "LEFT JOIN product_summary ps ON p.product_id = ps.product_id " +
            "LEFT JOIN product_category pc ON p.category_id = pc.category_id " +
            "ORDER BY p.product_id desc ")
	List<ProductRes> getAllProduct(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT * FROM product_detail " +
            "WHERE product_id IN " +
            "<foreach item='id' collection='productIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>")
    List<ProductDetailRes> getProductDetailsByProductIds(@Param("productIds") List<Long> productIds);

    @Select("SELECT * FROM product WHERE product_id = #{productId}")
    ProductRes getProductById(@Param("productId") Long productId);

    @Insert("INSERT INTO product (" +
            "product_name, description, price, stock_quantity, sold_quantity, " +
            "image_url, rarity, created_at, start_date, end_date, created_user , product_type , prize_category , status) " +
            "VALUES (#{productName}, #{description}, #{price}, #{stockQuantity}, #{soldQuantity}, " +
            "#{imageUrl}, #{rarity}, #{createdAt}, #{startDate}, #{endDate}, #{createdUser} , #{productType} , #{prizeCategory} , #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "productId")
    void createProduct(Product product);


    @Update("UPDATE product SET " +
            "product_name = #{productName}, " +
            "description = #{description}, " +
            "price = #{price}, " +
            "stock_quantity = #{stockQuantity}, " +
            "sold_quantity = #{soldQuantity}, " +
            "image_url = #{imageUrl}, " +
            "rarity = #{rarity}, " +
            "updated_at = #{updatedAt}, " +
            "start_date = #{startDate}, " +
            "end_date = #{endDate}, " +
            "update_user = #{updateUser} " +
            "product_type = #{productType}" +
            "prize_category = #{prizeCategory}" +
            "status = #{status]" +
            "WHERE product_id = #{productId}")
    void updateProduct(Product product);

    @Delete("DELETE FROM product WHERE product_id = #{productId}")
    void deleteProduct(@Param("productId") Integer productId);

    @Update("UPDATE product SET " +
            "stock_quantity = #{stockQuantity} " +
            "WHERE product_id = #{productId}")
    void updateProductQuantity(ProductRes product);
    @Update("update product set status = 'SOLD_OUT' where product_id = #{productId}")
    void updateStatus(Long productId);
    @Select("select category_id from product_category where category_uuid = #{uuid}")
    Long getProductByCategoryId(String uuid);
    @Select("SELECT * FROM product WHERE category_id = #{categoryId}")
    ProductRes getProductByCId(Long categoryId);
    @Select("SELECT p.*, " +
            "       p.stock_quantity AS detailQuantity " +
            "FROM product p " +
            "WHERE product_type = #{type} " +
            "ORDER BY CASE WHEN status = 'NOT_AVAILABLE_YET' THEN 1 ELSE 0 END, " +
            "         p.product_id DESC")
    List<ProductRes> getProductByType(String type);


    @Select("""
        WITH product_summary AS (
            SELECT product_id,
                   SUM(quantity) AS detailQuantity,
                   SUM(stock_quantity) AS detailStockQuantity
            FROM product_detail
            WHERE grade <> 'LAST'
            GROUP BY product_id
        ),
        first_detail AS (
            SELECT pd.*
            FROM (
                SELECT product_id,
                       quantity AS detailQuantityPerGrade,
                       stock_quantity AS detailStockQuantityPerGrade,
                       grade,
                       product_name,
                       ROW_NUMBER() OVER (PARTITION BY product_id ORDER BY grade) AS rn
                FROM product_detail
            ) pd
            WHERE pd.rn = 1
        )
        SELECT p.product_id AS productId,
               pc.category_uuid AS categoryUuid,
               COALESCE(ps.detailQuantity, 0) AS detailQuantity,
               COALESCE(ps.detailStockQuantity, 0) AS detailStockQuantity,
               fd.detailQuantityPerGrade,
               fd.detailStockQuantityPerGrade,
               fd.grade,
               fd.product_name AS productName,
               p.status,
               p.create_date AS createDate,
               p.update_date AS updateDate
        FROM product p
        LEFT JOIN product_summary ps ON p.product_id = ps.product_id
        LEFT JOIN product_category pc ON p.category_id = pc.category_id
        LEFT JOIN first_detail fd ON p.product_id = fd.product_id
        ORDER BY CASE WHEN p.status = 'NOT_AVAILABLE_YET' THEN 1 ELSE 0 END,
                 p.product_id DESC
    """)
    List<ProductRes> getAll();


    @Update("update product set status = 'NOT_AVAILABLE_YET' where product_id = #{productId}")
    void updateProductStatus(Long productId);
}
