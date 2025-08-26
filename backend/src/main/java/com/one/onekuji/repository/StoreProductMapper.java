package com.one.onekuji.repository;

import com.one.onekuji.model.StoreProduct;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StoreProductMapper {

    @Select("SELECT * FROM store_product order by created_at desc")
    List<StoreProduct> findAll();

    @Select("SELECT * FROM store_product WHERE store_product_id = #{id}")
    StoreProduct findById(Long id);

    @Insert("INSERT INTO store_product (" +
            "product_name, description, price, stock_quantity, image_urls, category_id, status, special_price, shipping_method, size, shipping_price, length, width, height, specification, created_at, updated_at, created_user, update_user,product_code,details) " +
            "VALUES (" +
            "#{productName}, #{description}, #{price}, #{stockQuantity}, " +
            "#{imageUrls}, #{categoryId}, #{status}, #{specialPrice}, #{shippingMethod}, #{size}, " +
            "#{shippingPrice}, #{length}, #{width}, #{height}, #{specification}, " +
            "#{createdAt}, #{updatedAt}, #{createdUser}, #{updateUser},#{productCode} , #{details})")
    @Options(useGeneratedKeys = true, keyProperty = "storeProductId")
    void insert(StoreProduct storeProduct);

    @Update("UPDATE store_product SET " +
            "product_name=#{productName}, description=#{description}, price=#{price}, stock_quantity=#{stockQuantity}, " +
            "image_urls=#{imageUrls}, category_id=#{categoryId}, status=#{status}, special_price=#{specialPrice}, " +
            "shipping_method=#{shippingMethod}, size=#{size}, shipping_price=#{shippingPrice}, length=#{length}, " +
            "width=#{width}, height=#{height}, specification=#{specification}, " +
            "updated_at=#{updatedAt}, update_user=#{updateUser} , details = #{details} " +
            "WHERE store_product_id=#{storeProductId}")
    void update(StoreProduct storeProduct);


    @Delete("DELETE FROM store_product WHERE store_product_id = #{id}")
    void delete(Long id);
    @Delete("DELETE FROM store_product WHERE category_id = #{id}")
    void deleteByCategory(Long categoryId);

    @Select("SELECT * FROM store_product WHERE category_id = #{id}")
    List<StoreProduct> getProductByCategoryId(Long id);
}
