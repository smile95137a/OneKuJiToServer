package com.one;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.one.dto")
@MapperScan(basePackages = {"com.one.repository"})
public class CommonEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonEndApplication.class, args);
    }
}