package com.one.onekuji.service;

import com.one.onekuji.dto.ProductDTO;
import com.one.onekuji.eenum.PrizeCategory;
import com.one.onekuji.eenum.ProductStatus;
import com.one.onekuji.eenum.ProductType;
import com.one.onekuji.model.Product;
import com.one.onekuji.model.ProductDetail;
import com.one.onekuji.repository.PrizeNumberMapper;
import com.one.onekuji.repository.ProductDetailRepository;
import com.one.onekuji.repository.ProductRepository;
import com.one.onekuji.repository.UserRepository;
import com.one.onekuji.request.DetailReq;
import com.one.onekuji.request.ProductReq;
import com.one.onekuji.response.ProductRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrizeNumberMapper prizeNumberMapper;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    public List<ProductRes> getAllProductByType(ProductType productType) {
        return productRepository.getAllProductByType(productType);
    }

    public List<ProductRes> getOneKuJiType(PrizeCategory type) {
        return productRepository.getOneKuJiType(type);
    }

    public ProductRes createProduct(ProductReq productReq) {
        try {
            Product product = new Product();
            convertReqToEntity(productReq, product);
            productRepository.insertProduct(product);
            return convertEntityToRes(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ProductRes> getAllProduct() {
        List<Product> products = productRepository.selectAllProducts();
        return products.stream().map(this::convertEntityToRes).collect(Collectors.toList());
    }

    public ProductRes getProductById(Long id) {
        Product product = productRepository.selectProductById(id);
        return product != null ? convertEntityToRes(product) : null;
    }

    public ProductRes updateProduct(Long id, ProductReq productReq) {
        Product product = productRepository.selectProductById(id);
        if (product != null) {
            convertReqToEntity(productReq, product);
            productRepository.updateProduct(product);
            return convertEntityToRes(product);
        }
        return null;
    }
    
    public ProductRes uploadProductImg(Long id, List<String> paths) {
        Product product = productRepository.selectProductById(id);
        if (product != null) {
            product.setImageUrls(paths);
            productRepository.updateProduct(product);
            return convertEntityToRes(product);
        }
        return null;
    }
    
    public ProductRes uploadProductBannerImg(Long id, List<String> paths) {
        Product product = productRepository.selectProductById(id);
        if (product != null) {
            product.setBannerImageUrl(paths);
            productRepository.updateProduct(product);
            return convertEntityToRes(product);
        }
        return null;
    }

    public boolean deleteProduct(Long id) {
        Product product = productRepository.getProductById(id);
        if (product == null) {
            return false;
        }
        prizeNumberMapper.deleteProductById(id);
        productDetailRepository.deleteProductDetailByProductId(Math.toIntExact(id));
        productRepository.deleteProduct(id);

        return true;
    }

    private void convertReqToEntity(ProductReq req, Product product) {
        product.setProductName(req.getProductName());
        product.setDescription((req.getDescription()));
        product.setPrice(req.getPrice() != null ? BigDecimal.valueOf(req.getPrice()) : BigDecimal.ZERO);
        product.setSliverPrice(req.getSliverPrice() != null ? req.getSliverPrice() : BigDecimal.ZERO);
        product.setStockQuantity(req.getStockQuantity());
        product.setImageUrls(req.getImageUrls());
        product.setProductType(req.getProductType());
        product.setPrizeCategory(req.getPrizeCategory());
        product.setStatus(req.getStatus());
        product.setBonusPrice(req.getBonusPrice() != null ? req.getBonusPrice() : BigDecimal.ZERO);
        product.setSpecification((req.getSpecification()));
        product.setBannerImageUrl(req.getBannerImageUrl());
        if(req.getProductType().equals(ProductType.GACHA)){
            product.setCategoryId(0L);
        }else{
            product.setCategoryId(req.getCategoryId());
        }

    }


    private ProductRes convertEntityToRes(Product product) {
        List<ProductDetail> productDetailByProductId = productDetailRepository.getProductDetailByProductId(Long.valueOf(product.getProductId()));
        int totalQuantity = productDetailByProductId.stream()
                .mapToInt(ProductDetail::getQuantity)
                .sum();
        product.setStockQuantity(totalQuantity);
        return new ProductRes(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getSliverPrice(),
                product.getStockQuantity(),
                product.getImageUrls(),
                product.getProductType(),
                product.getPrizeCategory(),
                product.getStatus(),
                product.getBonusPrice(),
                product.getSpecification(),
                product.getCategoryId(),
                product.getBannerImageUrl()
        );
    }

    private String escapeTextForHtml(String text) {
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


    public void duplicateProduct(ProductRes originalProduct) {
        Product duplicatedProduct = new Product();
        duplicatedProduct.setProductName(originalProduct.getProductName());
        duplicatedProduct.setDescription(originalProduct.getDescription());
        duplicatedProduct.setPrice(originalProduct.getPrice());
        duplicatedProduct.setSliverPrice(originalProduct.getSliverPrice());
        duplicatedProduct.setImageUrls(originalProduct.getImageUrls());
        duplicatedProduct.setCreatedAt(LocalDateTime.now());
        duplicatedProduct.setProductType(originalProduct.getProductType());
        duplicatedProduct.setPrizeCategory(originalProduct.getPrizeCategory());
        duplicatedProduct.setStatus(ProductStatus.UNAVAILABLE); // 設置為未上架
        duplicatedProduct.setBonusPrice(originalProduct.getBonusPrice());
        duplicatedProduct.setSpecification(originalProduct.getSpecification());
        duplicatedProduct.setCategoryId(originalProduct.getCategoryId());

        int result = productRepository.duplicateProduct(duplicatedProduct);

        if (result <= 0) {
            throw new RuntimeException("Failed to duplicate product");
        }

        // 從對象中獲取生成的 ID
        Long newProductId = Long.valueOf(duplicatedProduct.getProductId());

        if (newProductId == null || newProductId == 0) {
            throw new RuntimeException("Failed to get generated product ID");
        }

        // 獲取原始產品的詳細資料
        List<ProductDetail> productDetails = productDetailRepository.getProductDetailByProductId(
                Long.valueOf(originalProduct.getProductId())
        );

        // 使用生成的 ID
        List<DetailReq> detailReqs = convertToDetailReqList(productDetails, newProductId);

        // 插入每個詳細資料
        for (DetailReq detailReq : detailReqs) {
            productDetailRepository.insert(detailReq);
        }
    }


    public List<DetailReq> convertToDetailReqList(List<ProductDetail> productDetails , Long id) {
        List<DetailReq> detailReqs = new ArrayList<>();
        for (ProductDetail detail : productDetails) {
            DetailReq detailReq = new DetailReq();
            detailReq.setProductId(Math.toIntExact(id));
            detailReq.setDescription(detail.getDescription());
            detailReq.setNote(detail.getNote());
            detailReq.setSize(detail.getSize());
            detailReq.setQuantity(0);
            detailReq.setStockQuantity(0);
            detailReq.setProductName(detail.getProductName());
            detailReq.setGrade(detail.getGrade());
            detailReq.setPrice(detail.getPrice());
            detailReq.setSliverPrice(detail.getSliverPrice());
            detailReq.setImageUrls(detail.getImageUrls());
            detailReq.setLength(detail.getLength());
            detailReq.setWidth(detail.getWidth());
            detailReq.setHeight(detail.getHeight());
            detailReq.setSpecification(detail.getSpecification());
            detailReq.setProbability(detail.getProbability());
            detailReqs.add(detailReq);
        }
        return detailReqs;
    }


    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.selectProductById(id);
        product.setPrice(productDTO.getPrice());
        product.setSliverPrice(productDTO.getSliverPrice());
        product.setStatus(productDTO.getStatus());
        productRepository.updateProductPrice(product);
        return product;
    }
}
