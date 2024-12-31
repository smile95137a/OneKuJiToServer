package com.one.service;

import com.one.frontend.eenum.ProductStatus;
import com.one.frontend.repository.ProductDetailRepository;
import com.one.frontend.repository.ProductRepository;
import com.one.frontend.repository.UserRepository;
import com.one.frontend.response.ProductDetailRes;
import com.one.frontend.response.ProductRes;
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
