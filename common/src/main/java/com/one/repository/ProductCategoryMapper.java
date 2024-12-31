package com.one.repository;

import com.one.frontend.model.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductCategoryMapper {

    // 查詢所有類別
    @Select("SELECT * FROM product_category ORDER BY product_sort ASC")
    List<ProductCategory> getAllCategories();

    // 根據ID查詢類別
    @Select("SELECT * FROM product_category WHERE category_UUid = #{categoryId}")
    ProductCategory getCategoryById(String categoryId);

}
