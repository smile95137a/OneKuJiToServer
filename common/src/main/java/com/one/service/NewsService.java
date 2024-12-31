package com.one.service;

import com.one.frontend.model.News;
import com.one.frontend.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;
    // 获取所有新闻
    public List<News> getAllNews() {
        return newsRepository.getAllNews();
    }

    // 根据ID获取新闻
    public News getNewsById(String newsUid) {
        return newsRepository.getNewsById(newsUid);
    }
}
