package com.one.onekuji.service;

import com.one.onekuji.dto.ProductCategoryResponse;
import com.one.onekuji.model.ProductCategory;
import com.one.onekuji.repository.ProductCategoryMapper;
import com.one.onekuji.repository.ProductRepository;
import com.one.onekuji.response.ProductRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // 查詢所有類別
    public ProductCategoryResponse getAllCategories() {
        List<ProductCategory> allCategories = productCategoryMapper.getAllCategories();

        // 计算最大排序值，排除 "其他" 类别
        Long maxSort = allCategories.stream()
                .filter(category -> !"其他".equals(category.getCategoryName()))
                .mapToLong(ProductCategory::getProductSort)
                .max()
                .orElse(0L);

        return new ProductCategoryResponse(allCategories, maxSort);
    }


    // 根據ID查詢類別
    public ProductCategory getCategoryById(Long categoryId) {
        return productCategoryMapper.getCategoryById(categoryId);
    }

    // 創建新類別
    public ProductCategory createCategory(ProductCategory category) {
        category.setCategoryUUid(UUID.randomUUID().toString());
        List<ProductCategory> allCategories = productCategoryMapper.getAllCategories();

        // 提取已有的排序号
        Set<Long> existingSortNumbers = allCategories.stream()
                .map(ProductCategory::getProductSort)
                .collect(Collectors.toSet());

        // 若用户指定了排序号，则检查冲突情况
        if (category.getProductSort() != null && !existingSortNumbers.contains(category.getProductSort())) {
            category.setProductSort(category.getProductSort());
        } else {
            // 若未指定或冲突，则将其放置在末尾
            Long newSortNumber = 1L;
            while (existingSortNumbers.contains(newSortNumber)) {
                newSortNumber++;
            }
            category.setProductSort(newSortNumber);
        }

        productCategoryMapper.createCategory(category);
        return category;
    }


    // 更新類別
    public ProductCategory updateCategory(Long categoryId, ProductCategory category) {
        // 获取现有的类别
        ProductCategory existingCategory = productCategoryMapper.getCategoryById(categoryId);
        if (existingCategory != null) {
            category.setCategoryId(categoryId); // 确保更新时保持 ID 一致
            category.setCategoryUUid(existingCategory.getCategoryUUid()); // 保留原始 UUID

            // 检查是否有重复的 productSort
            List<ProductCategory> allCategories = productCategoryMapper.getAllCategories();
            boolean sortExists = allCategories.stream()
                    .anyMatch(cat -> !cat.getCategoryId().equals(categoryId)
                            && cat.getProductSort().equals(category.getProductSort()));

            if (sortExists) {
                // 如果 sort 已存在，可以选择抛出异常，或自动调整排序号
                throw new IllegalArgumentException("排序号已存在，请选择一个不同的排序号。");
            }

            // 更新类别信息
            productCategoryMapper.updateCategory(category);
            return category;
        }
        return null;
    }


    // 刪除類別
    public boolean deleteCategory(Long categoryId) {
        ProductCategory existingCategory = productCategoryMapper.getCategoryById(categoryId);
        ProductRes res =  productRepository.getProductByCategoryId(categoryId);
        if(res != null){
            productService.deleteProduct(Long.valueOf(res.getProductId()));
        }
        if (existingCategory != null) {

            productCategoryMapper.deleteCategory(categoryId);
                return true;
        }
        return false;
    }
}
