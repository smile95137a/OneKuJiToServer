package com.one.onekuji.service;

import com.one.onekuji.model.StoreProductRecommendation;
import com.one.onekuji.repository.ProductRecommendationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductRecommendationService {

    @Autowired
    private ProductRecommendationMapper recommendationMapper;

    public List<StoreProductRecommendation> getAllRecommendations() {
        return recommendationMapper.getAllRecommendations();
    }

    public StoreProductRecommendation getRecommendationById(Long id) {
        return recommendationMapper.getRecommendationById(id);
    }

    public StoreProductRecommendation createRecommendation(StoreProductRecommendation recommendation) {
        recommendation.setCreatedDate(LocalDateTime.now());
        recommendation.setUpdatedDate(LocalDateTime.now());
        recommendationMapper.insertRecommendation(recommendation);
        return recommendation;
    }

    public StoreProductRecommendation updateRecommendation(Long id, StoreProductRecommendation recommendation) {
        StoreProductRecommendation existing = recommendationMapper.getRecommendationById(id);
        if (existing == null) {
            return null;
        }
        StoreProductRecommendation storeProductRecommendation = new StoreProductRecommendation();
        storeProductRecommendation.setId(id);
        storeProductRecommendation.setRecommendationName(recommendation.getRecommendationName());
        storeProductRecommendation.setUpdateUser(recommendation.getUpdateUser());
        storeProductRecommendation.setUpdatedDate(LocalDateTime.now());
        recommendationMapper.updateRecommendation(storeProductRecommendation);
        return recommendation;
    }

    public boolean deleteRecommendation(Long id) {
        StoreProductRecommendation existing = recommendationMapper.getRecommendationById(id);
        if (existing == null) {
            return false;
        }
        recommendationMapper.deleteRecommendation(id);
        return true;
    }
}
