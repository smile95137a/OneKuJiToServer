package com.one.onekuji.dto;

import com.one.onekuji.model.ProductCategory;

import java.util.List;

public class ProductCategoryResponse {
    private List<ProductCategory> categories;
    private Long maxProductSort;

    // Constructor, Getters, and Setters
    public ProductCategoryResponse(List<ProductCategory> categories, Long maxProductSort) {
        this.categories = categories;
        this.maxProductSort = maxProductSort;
    }

    public List<ProductCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ProductCategory> categories) {
        this.categories = categories;
    }

    public Long getMaxProductSort() {
        return maxProductSort;
    }

    public void setMaxProductSort(Long maxProductSort) {
        this.maxProductSort = maxProductSort;
    }
}
