package com.one.onekuji.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class URLConfig implements WebMvcConfigurer {
    @org.springframework.beans.factory.annotation.Value("${pictureFile.path}")
    private String picturePath;

    @Value("${pictureFile.path-mapping}")
    private String picturePath_mapping;

    @Value("${pictureFile.storage-type:local}")
    private String pictureStorageType;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        if ("local".equalsIgnoreCase(pictureStorageType)) {
            registry.addResourceHandler(picturePath_mapping + "**").addResourceLocations("file:" + picturePath);
        }

    }

}