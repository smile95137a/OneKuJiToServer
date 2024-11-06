package com.one.onekuji.service;

import com.one.onekuji.model.Banner;
import com.one.onekuji.model.Product;
import com.one.onekuji.repository.BannerRepository;
import com.one.onekuji.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private ProductRepository productRepository;

    public Banner findById(String bannerUid) {
        return bannerRepository.findById(bannerUid);
    }

    public List<Banner> findAll() {
        return bannerRepository.findAll();
    }

    public void createBanner(Banner banner) {
        try{
            Product productById = productRepository.getProductById(banner.getProductId());
            banner.setProductType(productById.getProductType());
            banner.setImageUrls(productById.getBannerImageUrl());
            banner.setBannerUid(UUID.randomUUID().toString());
            banner.setCreatedAt(LocalDateTime.now());
            banner.setUpdatedAt(LocalDateTime.now());
            bannerRepository.insert(banner);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void updateBanner(String bannerUid , Banner banner) {
        Banner reqBanner = bannerRepository.findById(bannerUid);
        Product productById = productRepository.getProductById(banner.getProductId());
        reqBanner.setProductType(productById.getProductType());
        reqBanner.setImageUrls(productById.getBannerImageUrl());
        reqBanner.setProductId(banner.getBannerId());
        reqBanner.setStatus(banner.getStatus());
        reqBanner.setUpdatedAt(LocalDateTime.now());

        bannerRepository.update(reqBanner);
    }

    public void deleteBanner(Long bannerId) {
        bannerRepository.deleteById(bannerId);
    }
}
