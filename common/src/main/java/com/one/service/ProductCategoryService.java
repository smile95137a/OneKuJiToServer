package com.one.service;

import com.one.model.ProductCategory;
import com.one.repository.ProductCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    // 查詢所有類別
    public List<ProductCategory> getAllCategories() {
        return productCategoryMapper.getAllCategories();
    }

    // 根據ID查詢類別
    public ProductCategory getCategoryById(String categoryId) {
        return productCategoryMapper.getCategoryById(categoryId);
    }

}
