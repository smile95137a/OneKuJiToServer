package com.one.onekuji.service;

import com.one.onekuji.model.News;
import com.one.onekuji.repository.NewsRepository;
import com.one.onekuji.util.HtmlProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    // 新增新闻
    public int insertNews(News news) {
        // 使用 Jsoup 处理 content 字段，补全图片 URL
        String processedContent = HtmlProcessor.processHtml(news.getContent());

        // 设置处理后的 content 字段
        news.setContent(processedContent);

        // 设置其余的新闻属性
        news.setNewsUid(UUID.randomUUID().toString());
        news.setCreatedDate(LocalDateTime.now());
        news.setUpdatedDate(LocalDateTime.now());

        // 保存新闻记录
        return newsRepository.insertNews(news);
    }


    // 更新新闻
    public int updateNews(String newsUid, News news) {
        News the_news = newsRepository.getNewsById(newsUid);

        // 使用 Jsoup 处理 content 字段，补全图片 URL
        String processedContent = HtmlProcessor.processHtml(news.getContent());

        // 更新新闻对象的属性
        the_news.setNewsUid(newsUid);
        the_news.setContent(processedContent);  // 设置处理后的 content
        the_news.setImageUrls(news.getImageUrls());
        the_news.setTitle(news.getTitle());
        the_news.setPreview(news.getPreview());
        the_news.setUpdatedDate(LocalDateTime.now());
        the_news.setStatus(news.getStatus());
        the_news.setStartDate(news.getStartDate()); // 确保设置 startDate
        the_news.setEndDate(news.getEndDate());
        the_news.setIsDisplayOnHome(news.getIsDisplayOnHome());

        // 更新新闻记录
        return newsRepository.updateNews(the_news);
    }



    // 删除新闻
    public int deleteNews(String newsUid) {
        return newsRepository.deleteNews(newsUid);
    }

//    private String formatTextToHtml(String text) {
//        if (text == null) return "";
//        // 先保留換行符的處理
//        String escapedText = text.replace("\n", "<br/>").replace("\r", "");
//
//        // 再進行其他字符轉義
//        return escapedText.replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;")
//                .replace("\"", "&quot;")
//                .replace("'", "&#39;")
//                // 恢復 <br/> 原來的形式，避免被轉義
//                .replace("&lt;br/&gt;", "<br/>");
//    }

}
