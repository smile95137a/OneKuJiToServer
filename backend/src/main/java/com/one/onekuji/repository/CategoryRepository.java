package com.one.onekuji.repository;

import com.one.onekuji.model.StoreCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryRepository {

    // 獲取所有類別
    @Select("SELECT category_id, category_name FROM store_category")
    List<StoreCategory> getAllCategory();

    // 根據ID獲取類別
    @Select("SELECT category_id, category_name FROM store_category WHERE category_id = #{categoryId}")
    StoreCategory getCategoryById(@Param("categoryId") Long categoryId);

    // 插入新的類別
    @Insert("INSERT INTO store_category(category_name) VALUES(#{categoryName})")
    @Options(useGeneratedKeys = true, keyProperty = "categoryId")
    void insertCategory(StoreCategory category);

    // 更新類別
    @Update("UPDATE store_category SET category_name = #{categoryName} WHERE category_id = #{categoryId}")
    void updateCategory(StoreCategory category);

    // 刪除類別
    @Delete("DELETE FROM store_category WHERE category_id = #{categoryId}")
    void deleteCategory(@Param("categoryId") Long categoryId);
}
