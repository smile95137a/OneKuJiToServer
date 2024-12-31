package com.one.service;

import com.one.eenum.ProductStatus;
import com.one.repository.ProductDetailRepository;
import com.one.repository.ProductRepository;
import com.one.repository.UserRepository;
import com.one.response.ProductDetailRes;
import com.one.response.ProductRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductDetailService {

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ProductDetailRes> getAllProductDetail() {
        return productDetailRepository.getAllProductDetail();
    }

    public List<ProductDetailRes> getProductDetailByProductId(Long productId) {
        ProductRes productById = productRepository.getProductById(productId);
        if(productById.getStatus() == ProductStatus.UNAVAILABLE){
            return null;
        }
        return productDetailRepository.getProductDetailByProductId(productId);
    }
}
