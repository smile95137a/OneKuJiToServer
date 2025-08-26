package com.one.frontend.controller;

import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.Banner;
import com.one.frontend.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<Banner>>> getAllBanners() {
        try {
            List<Banner> banners = bannerService.findAll();
            ApiResponse<List<Banner>> response = new ApiResponse<>(200, "找不到banner", true, banners);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<Banner>> response = new ApiResponse<>(400, "No banners found", false, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
