package com.one.frontend.repository;

import com.one.frontend.model.ProductCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductCategoryMapper {

    // 查詢所有類別
    @Select("SELECT * FROM product_category")
    List<ProductCategory> getAllCategories();

    // 根據ID查詢類別
    @Select("SELECT * FROM product_category WHERE category_UUid = #{categoryId}")
    ProductCategory getCategoryById(String categoryId);

}
