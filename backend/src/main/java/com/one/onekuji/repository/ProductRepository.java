package com.one.onekuji.repository;

import com.one.onekuji.eenum.PrizeCategory;
import com.one.onekuji.eenum.ProductType;
import com.one.onekuji.model.Product;
import com.one.onekuji.response.ProductRes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductRepository {
    @Insert("INSERT INTO product (" +
            "product_name, description, price, sliver_price, stock_quantity, image_urls, " +
            "product_type, prize_category, status, bonus_price, specification) " +
            "VALUES (" +
            "#{productName}, #{description}, #{price}, #{sliverPrice}, #{stockQuantity}, #{imageUrls}, " +
            "#{productType}, #{prizeCategory}, #{status}, #{bonusPrice}, #{length}, #{specification})")
    @Options(useGeneratedKeys = true, keyProperty = "productId")
    int insertProduct(Product product);



    @Select("SELECT * FROM product WHERE product_id = #{id}")
    Product selectProductById(Long id);

    @Select("SELECT * FROM product")
    List<Product> selectAllProducts();

    @Select("SELECT * FROM product WHERE product_id = #{productId}")
    Product getProductById(@Param("productId") Long productId);

    @Update("UPDATE product SET " +
            "product_name = #{productName}, " +
            "description = #{description}, " +
            "price = #{price}, " +
            "sliver_price = #{sliverPrice}, " +
            "stock_quantity = #{stockQuantity}, " +
            "image_urls = #{imageUrls}, " +
            "product_type = #{productType}, " +
            "prize_category = #{prizeCategory}, " +
            "status = #{status}, " +
            "bonus_price = #{bonusPrice}, " +
            "specification = #{specification} " +
            "WHERE product_id = #{productId}")
    void updateProduct(Product product);



    @Delete("DELETE FROM product WHERE product_id = #{productId}")
    void deleteProduct(@Param("productId") Long productId);

    @Update("UPDATE product SET " +
            "stock_quantity = #{stockQuantity}, " +
            "sold_quantity = #{soldQuantity} " +
            "WHERE product_id = #{productId}")
    void updateProductQuantity(Product product);

    @Select("select * from product where product_type = #{productType}")
    List<ProductRes> getAllProductByType(ProductType productType);

    @Update("UPDATE `product` SET `image_url` = #{base64} WHERE `product_id` IN (1,2,3,4,5,6,7,8,9,10,11,12,13)")
    void updateImageUrlForProducts(@Param("base64") String base64);

    @Select("select * from product where product_type = 'PRIZE' and prize_category = #{type}")
    List<ProductRes> getOneKuJiType(PrizeCategory type);

    @Update("update `product` set stock_quantity = #{totalQuantity} where product_id = #{id}")
    void updateTotalQua(int totalQuantity , Integer id);
    //ENUM('FIGURE', 'BONUS', 'C3')
}
