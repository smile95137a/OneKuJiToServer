package com.one.onekuji.service;

import com.one.onekuji.model.StoreCategory;
import com.one.onekuji.model.StoreProduct;
import com.one.onekuji.repository.ProductRecommendationMappingMapper;
import com.one.onekuji.repository.StoreProductMapper;
import com.one.onekuji.request.StoreProductReq;
import com.one.onekuji.response.StoreProductRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StoreProductService {

    @Autowired
    private StoreProductMapper storeProductMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductRecommendationMappingMapper productRecommendationMappingMapper;


    public List<StoreProductRes> getAllStoreProduct() {
        List<StoreProduct> storeProductList = storeProductMapper.findAll();
        return storeProductList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    public StoreProductRes addStoreProduct(StoreProductReq storeProductReq) throws Exception {

        StoreCategory category = categoryService.getCategoryById(Long.valueOf(storeProductReq.getCategoryId()));
        if(category == null){
            throw new Exception("查無此類別，不能新增");
        }
        StoreProduct storeProduct = convertToEntity(storeProductReq);
        storeProduct.setCreatedAt(LocalDateTime.now());
        storeProduct.setUpdatedAt(LocalDateTime.now());
        storeProduct.setSoldQuantity(0);
        storeProductMapper.insert(storeProduct);
        return convertToResponse(storeProduct);
    }

    public StoreProductRes updateStoreProduct(Long id, StoreProductReq storeProductReq) throws Exception {
        StoreProduct storeProduct = storeProductMapper.findById(id);
        if (storeProduct == null) {
            return null;
        }

        StoreCategory category = categoryService.getCategoryById(Long.valueOf(storeProductReq.getCategoryId()));
        if(category == null){
            throw new Exception("查無此類別，不能新增");
        }

        String description = formatTextToHtml(storeProductReq.getDescription());
        String specification = formatTextToHtml(storeProductReq.getSpecification());

        BigDecimal height = storeProductReq.getHeight() != null ? storeProductReq.getHeight() : BigDecimal.ZERO;
        BigDecimal width = storeProductReq.getWidth() != null ? storeProductReq.getWidth() : BigDecimal.ZERO;
        BigDecimal length = storeProductReq.getLength() != null ? storeProductReq.getLength() : BigDecimal.ZERO;
        BigDecimal size = storeProductReq.getSize() != null ? storeProductReq.getSize() : BigDecimal.ZERO;
        if (size.compareTo(BigDecimal.ZERO) > 0) {
            // 保證當 size 小於 10 時，不會計算出 0，維持最小尺寸為 1
            BigDecimal dimension = size.subtract(BigDecimal.TEN).divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP);
            // 設置長度、寬度和高度
            length = dimension.max(BigDecimal.ONE);  // 最小為 1
            width = dimension.max(BigDecimal.ONE);   // 最小為 1
            height = BigDecimal.valueOf(2);  // 高度固定為 2
        }


        // Perform the multiplication using BigDecimal methods
        BigDecimal calculatedSize  = height.multiply(width).multiply(length);
        // Update fields from request
        storeProduct.setProductName(storeProductReq.getProductName());
        storeProduct.setDescription(description);
        storeProduct.setHeight(storeProductReq.getHeight());
        storeProduct.setLength(storeProductReq.getLength());
        storeProduct.setWidth(storeProductReq.getWidth());
        storeProduct.setPrice(storeProductReq.getPrice());
        storeProduct.setStockQuantity(storeProductReq.getStockQuantity());
        storeProduct.setImageUrls(storeProductReq.getImageUrl());
        storeProduct.setCategoryId(storeProductReq.getCategoryId());
        storeProduct.setStatus(String.valueOf(storeProductReq.getStatus()));
        storeProduct.setSpecialPrice(storeProductReq.getSpecialPrice());
        storeProduct.setShippingMethod(storeProductReq.getShippingMethod());
        storeProduct.setShippingPrice(storeProductReq.getShippingPrice());
        storeProduct.setSize(calculatedSize);
        storeProduct.setSpecification(specification);
        storeProduct.setUpdatedAt(LocalDateTime.now());
        storeProduct.setDetails(formatTextToHtml(storeProductReq.getDetails()));

        // Update the store product
        storeProductMapper.update(storeProduct);

        // Convert to response
        return convertToResponse(storeProduct);
    }

    public boolean deleteStoreProduct(Long id) {
        StoreProduct storeProduct = storeProductMapper.findById(id);
        if (storeProduct == null) {
            return false;
        }

        productRecommendationMappingMapper.delete(storeProduct.getStoreProductId());

        storeProductMapper.delete(id);
        return true;
    }

    private StoreProductRes convertToResponse(StoreProduct storeProduct) {
        return new StoreProductRes(
                storeProduct.getStoreProductId(),
                storeProduct.getProductName(),
                storeProduct.getDescription(),
                storeProduct.getPrice(),
                storeProduct.getStockQuantity(),
                storeProduct.getImageUrls(),
                storeProduct.getCategoryId(),
                storeProduct.getStatus(),
                storeProduct.getSpecialPrice(),
                storeProduct.getShippingMethod(),
                storeProduct.getSize(),
                storeProduct.getShippingPrice(),
                storeProduct.getLength(),
                storeProduct.getWidth(),
                storeProduct.getHeight(),
                storeProduct.getSpecification(),
                storeProduct.getDetails(),
                storeProduct.getSoldQuantity()

        );
    }

    private StoreProduct convertToEntity(StoreProductReq storeProductReq) {
        BigDecimal height = storeProductReq.getHeight() != null ? storeProductReq.getHeight() : BigDecimal.ZERO;
        BigDecimal width = storeProductReq.getWidth() != null ? storeProductReq.getWidth() : BigDecimal.ZERO;
        BigDecimal length = storeProductReq.getLength() != null ? storeProductReq.getLength() : BigDecimal.ZERO;
        BigDecimal size = storeProductReq.getSize() != null ? storeProductReq.getSize() : BigDecimal.ZERO;
        if (size.compareTo(BigDecimal.ZERO) > 0) {
            // 保證當 size 小於 10 時，不會計算出 0，維持最小尺寸為 1
            BigDecimal dimension = size.subtract(BigDecimal.TEN).divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP);
            // 設置長度、寬度和高度
            length = dimension.max(BigDecimal.ONE);  // 最小為 1
            width = dimension.max(BigDecimal.ONE);   // 最小為 1
            height = BigDecimal.valueOf(2);  // 高度固定為 2
        }
        String description = formatTextToHtml(storeProductReq.getDescription());
        String specification = formatTextToHtml(storeProductReq.getSpecification());
        BigDecimal calculatedSize  = height.multiply(width).multiply(length);
        StoreProduct storeProduct = new StoreProduct();
        storeProduct.setProductCode(UUID.randomUUID().toString());
        storeProduct.setHeight(height);
        storeProduct.setWidth(width);
        storeProduct.setLength(length);
        storeProduct.setProductName(storeProductReq.getProductName());
        storeProduct.setDescription(description);
        storeProduct.setPrice(storeProductReq.getPrice());
        storeProduct.setStockQuantity(storeProductReq.getStockQuantity());
        storeProduct.setImageUrls(storeProductReq.getImageUrl());
        storeProduct.setCategoryId(storeProductReq.getCategoryId());
        storeProduct.setStatus(String.valueOf(storeProductReq.getStatus()));
        storeProduct.setSpecialPrice(storeProductReq.getSpecialPrice());
        storeProduct.setSize(calculatedSize);
        storeProduct.setShippingMethod(storeProductReq.getShippingMethod());
        storeProduct.setShippingPrice(storeProductReq.getShippingPrice());
        storeProduct.setSpecification(specification);
        storeProduct.setDetails(formatTextToHtml(storeProductReq.getDetails()));
        return storeProduct;
    }

    private String formatTextToHtml(String text) {
        if (text == null) return "";
        // 先保留換行符的處理
        String escapedText = text.replace("\n", "<br/>").replace("\r", "");

        // 再進行其他字符轉義
        return escapedText.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                // 恢復 <br/> 原來的形式，避免被轉義
                .replace("&lt;br/&gt;", "<br/>");
    }

    public StoreProduct getProductById(Long id) {
        StoreProduct res = storeProductMapper.findById(id);
        return res;
    }
}
