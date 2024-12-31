package com.one.controller;

import com.one.frontend.config.security.CustomUserDetails;
import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.model.ApiResponse;
import com.one.frontend.model.News;
import com.one.frontend.service.NewsService;
import com.one.frontend.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<News>>> getAllNews() {
        try {
            List<News> newsList = newsService.getAllNews();
            ApiResponse<List<News>> response = ResponseUtils.success(200, null, newsList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse<List<News>> response = ResponseUtils.failure(500, "获取新闻列表失败", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{newsUid}")
    public ResponseEntity<ApiResponse<News>> getNewsById(@PathVariable String newsUid) {
        try {
            CustomUserDetails userDetails = SecurityUtils.getCurrentUserPrinciple();
            News news = newsService.getNewsById(newsUid);
            if (news != null) {
                ApiResponse<News> response = ResponseUtils.success(200, null, news);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<News> response = ResponseUtils.failure(404, "新闻不存在", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            ApiResponse<News> response = ResponseUtils.failure(500, "获取新闻失败", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
