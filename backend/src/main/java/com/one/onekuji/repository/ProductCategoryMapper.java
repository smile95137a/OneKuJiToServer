package com.one.onekuji.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.one.onekuji.model.ProductCategory;

@Mapper
public interface ProductCategoryMapper {

    // 查詢所有類別
    @Select("SELECT *,\n" +
            "       (SELECT MAX(product_sort) \n" +
            "        FROM product_category \n" +
            "        WHERE category_id != 40) AS max_product_sort\n" +
            "FROM product_category \n" +
            "order by product_sort asc")
    List<ProductCategory> getAllCategories();


    // 根據ID查詢類別
    @Select("SELECT * FROM product_category WHERE category_id = #{categoryId}")
    ProductCategory getCategoryById(Long categoryId);

    // 創建新類別
    @Insert("INSERT INTO product_category (category_name, category_UUid , product_sort) VALUES (#{categoryName}, #{categoryUUid} , #{productSort})")
    @Options(useGeneratedKeys = true, keyProperty = "categoryId")
    void createCategory(ProductCategory category);

    // 更新類別
    @Update("UPDATE product_category SET category_name = #{categoryName}, category_UUid = #{categoryUUid} , product_sort = #{productSort} WHERE category_id = #{categoryId}")
    void updateCategory(ProductCategory category);

    // 刪除類別
    @Delete("DELETE FROM product_category WHERE category_id = #{categoryId}")
    void deleteCategory(Long categoryId);
}
