package com.one.onekuji.service;

import com.one.onekuji.model.ProductRecommendationMapping;
import com.one.onekuji.repository.ProductRecommendationMappingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductRecommendationMappingService {

    @Autowired
    private ProductRecommendationMappingMapper mapper;

    public List<ProductRecommendationMapping> getAllMappings() {
        return mapper.getAllMappings();
    }

    public ProductRecommendationMapping getMappingById(Long id) {
        return mapper.getMappingById(id);
    }

    public int createMapping(ProductRecommendationMapping mapping) {
        mapping.setCreatedDate(LocalDateTime.now());
        mapping.setUpdatedDate(LocalDateTime.now());
        return mapper.createMapping(mapping);
    }

    public int updateMapping(Long id, ProductRecommendationMapping mapping) {
        ProductRecommendationMapping existingMapping = mapper.getMappingById(id);
        if (existingMapping == null) {
            return 0;
        }
        ProductRecommendationMapping productRecommendationMapping = new ProductRecommendationMapping();
        productRecommendationMapping.setId(id);
        productRecommendationMapping.setStoreProductRecommendationId(mapping.getStoreProductRecommendationId());
        productRecommendationMapping.setStoreProductId(mapping.getStoreProductId());
        productRecommendationMapping.setUpdatedDate(LocalDateTime.now());
        productRecommendationMapping.setUpdateUser(mapping.getUpdateUser());
        return mapper.updateMapping(productRecommendationMapping);
    }

    public int deleteMapping(Long id) {
        return mapper.deleteMapping(id);
    }
}
