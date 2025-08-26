package com.one.frontend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.one.frontend.dto")
@MapperScan(basePackages = {"com.one.frontend.repository"})
public class FrontEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrontEndApplication.class, args);
    }
}