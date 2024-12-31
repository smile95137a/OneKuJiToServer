package com.one.service;

import com.one.frontend.model.Banner;
import com.one.frontend.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    public Banner findById(Long bannerId) {
        return bannerRepository.findById(bannerId);
    }

    public List<Banner> findAll() {
        return bannerRepository.findAll();
    }

    }
