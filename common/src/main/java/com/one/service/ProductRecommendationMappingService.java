package com.one.service;

import com.one.repository.ProductRecommendationMappingMapper;
import com.one.response.RecommendRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductRecommendationMappingService {

    @Autowired
    private ProductRecommendationMappingMapper mapper;

    public List<RecommendRes> getAllMappings() {
        return mapper.getAllMappings();
    }

    public List<RecommendRes> getMappingById(Long id) {
        List<RecommendRes> res = new ArrayList<>();
        if(id == 1 || id == 2 || id == 3){
            res = mapper.getMappingById(id);
        }else{
            res = mapper.getMappingById2(id);
        }
        return res;
    }
}
