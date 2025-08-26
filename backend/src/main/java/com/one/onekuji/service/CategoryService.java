package com.one.onekuji.service;

import com.one.onekuji.model.StoreCategory;
import com.one.onekuji.model.StoreProduct;
import com.one.onekuji.repository.CategoryRepository;
import com.one.onekuji.repository.ProductRecommendationMappingMapper;
import com.one.onekuji.repository.StoreProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRecommendationMappingMapper productRecommendationMappingMapper;

    @Autowired
    private StoreProductMapper storeProductMapper;

    // 獲取所有類別
    public List<StoreCategory> getAllCategory() {
        return categoryRepository.getAllCategory();
    }

    // 根據ID獲取類別
    public StoreCategory getCategoryById(Long id) {
        return categoryRepository.getCategoryById(id);
    }

    // 創建新的類別
    public StoreCategory createCategory(StoreCategory category) {
        categoryRepository.insertCategory(category);
        return category;
    }

    // 更新類別
    public StoreCategory updateCategory(Long id, StoreCategory category) {
        StoreCategory existingCategory = getCategoryById(id);
        if (existingCategory == null) {
            return null;
        }
        category.setCategoryId(id);
        categoryRepository.updateCategory(category);
        return category;
    }

    // 刪除類別
    public boolean deleteCategory(Long id) {
        StoreCategory existingCategory = getCategoryById(id);
        if (existingCategory == null) {
            return false;
        }
        List<StoreProduct> list = storeProductMapper.getProductByCategoryId(id);
        for(StoreProduct result : list){
            productRecommendationMappingMapper.delete(result.getStoreProductId());
        }
        storeProductMapper.deleteByCategory(existingCategory.getCategoryId());
        categoryRepository.deleteCategory(id);
        return true;
    }
}
