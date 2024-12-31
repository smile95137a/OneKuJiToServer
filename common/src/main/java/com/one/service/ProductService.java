package com.one.service;

import com.one.frontend.eenum.ProductStatus;
import com.one.frontend.eenum.ProductType;
import com.one.frontend.repository.ProductRepository;
import com.one.frontend.repository.UserRepository;
import com.one.frontend.response.ProductDetailRes;
import com.one.frontend.response.ProductRes;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    public List<ProductRes> getAllProduct(int page, int size) {
        int offset = page * size;
        try {
            return productRepository.getAllProduct(offset, size);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<ProductDetailRes> getProductDetailsByProductIds(List<Long> productIds) {
        try {
            return productRepository.getProductDetailsByProductIds(productIds);
        } catch (Exception e) {
            throw new ServiceException("獲取商品詳情失敗", e);
        }
    }

    public ProductRes getProductById(Long productId) {
        ProductRes productById = productRepository.getProductById(productId);
        if(productById.getStatus() == ProductStatus.UNAVAILABLE){
            return null;
        }
        return productById;
    }


    public ProductRes getProductByCategoryId(String uuid) {
        Long categoryId = productRepository.getProductByCategoryId(uuid);

        ProductRes res = productRepository.getProductByCId(categoryId);

        return res;
    }

    public List<ProductRes> getProductByType(String type) {
        // 将数字类型转换为对应的ProductType枚举
        ProductType productType = ProductType.fromCode(type);

        // 根据枚举值从数据库获取相应类型的商品
        return productRepository.getProductByType(productType.name());
    }

    public List<ProductRes> getAll() {
        return productRepository.getAll();
    }
}
