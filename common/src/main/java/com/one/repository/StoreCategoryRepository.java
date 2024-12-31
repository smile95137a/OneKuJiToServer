package com.one.repository;

import com.one.model.StoreCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StoreCategoryRepository {

    @Select("SELECT category_id AS categoryId, category_name AS categoryName FROM store_category")
    List<StoreCategory> findAll();

    @Select("SELECT category_id AS categoryId, category_name AS categoryName FROM store_category WHERE category_id = #{categoryId}")
    StoreCategory findById(Long categoryId);
}
