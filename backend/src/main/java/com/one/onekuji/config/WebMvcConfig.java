package com.one.onekuji.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置静态资源映射
 * @author cao
 * @since 2019/01/23
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
////将所有/static/** 访问都映射到classpath:/static/ 目录下
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /img/** 到外部目录 /home/ec2-user/img/
        registry.addResourceHandler("/img/**").addResourceLocations("file:/home/ec2-user/img/");
    }
}