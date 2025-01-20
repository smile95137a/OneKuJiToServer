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
    @Select("""
    WITH product_summary AS (
        SELECT 
            product_id,
            SUM(quantity) AS detailQuantity,           -- 總數量
            SUM(stock_quantity) AS detailStockQuantity -- 庫存總數量
        FROM product_detail
        WHERE grade <> 'LAST'                         -- 排除等級為 'LAST' 的產品
        GROUP BY product_id                           -- 按 product_id 分組
    )
    SELECT 
        p.*,                                          -- 主產品表的所有欄位
        pc.category_uuid,                             -- 類別的唯一識別碼
        COALESCE(ps.detailQuantity, 0) AS detailQuantity,       -- 若明細數量為 NULL，則返回 0
        COALESCE(ps.detailStockQuantity, 0) AS detailStockQuantity -- 若明細庫存為 NULL，則返回 0
    FROM 
        product p
    LEFT JOIN 
        product_summary ps ON p.product_id = ps.product_id -- 將主產品與明細彙總數據關聯
    LEFT JOIN 
        product_category pc ON p.category_id = pc.category_id -- 將產品與類別表關聯
    WHERE 
        p.product_type = #{type}             -- 篩選產品類型為 'CUSTMER_PRIZE'
    ORDER BY 
        CASE WHEN p.status = 'NOT_AVAILABLE_YET' THEN 1 ELSE 0 END, -- 尚未可用的產品優先排序
        p.product_id DESC                             -- 根據 product_id 倒序排列
    """)
    List<ProductRes> getProductByType(String type);


    @Select("WITH product_summary AS ( " +
            "  SELECT product_id, " +
            "    SUM(quantity) AS detailQuantity, " +
            "    SUM(stock_quantity) AS detailStockQuantity " +
            "  FROM product_detail " +
            "  WHERE grade <> 'LAST' " + // 移到 WITH 子句中，減少需要處理的數據量
            "  GROUP BY product_id " +
            ") " +
            "SELECT p.*, " +
            "       pc.category_uuid, " +
            "       COALESCE(ps.detailQuantity, 0) AS detailQuantity, " + // 使用 COALESCE 處理 NULL
            "       COALESCE(ps.detailStockQuantity, 0) AS detailStockQuantity " +
            "FROM product p " +
            "LEFT JOIN product_summary ps ON p.product_id = ps.product_id " +
            "LEFT JOIN product_category pc ON p.category_id = pc.category_id " +
            "ORDER BY CASE WHEN p.status = 'NOT_AVAILABLE_YET' THEN 1 ELSE 0 END, " +
            "         p.product_id DESC")
    List<ProductRes> getAll();


    @Update("update product set status = 'NOT_AVAILABLE_YET' where product_id = #{productId}")
    void updateProductStatus(Long productId);
}
