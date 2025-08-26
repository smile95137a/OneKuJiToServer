package com.one.frontend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.one.frontend.model.News;

@Mapper
public interface NewsRepository {

    // 查询所有新闻
    @Select("SELECT * FROM news WHERE status = 'AVAILABLE' ORDER BY created_date DESC")
    List<News> getAllNews();

    // 根据ID查询新闻
    @Select("SELECT * FROM news WHERE news_uid = #{newsUid}")
    News getNewsById(String newsUid);

    // 查询符合条件的新闻
    @Select("SELECT * FROM news " +
            "WHERE #{date} BETWEEN start_date AND end_date " +
            "AND is_display_on_home = TRUE AND status = 'AVAILABLE'")
    List<News> findNewsByDateAndDisplayOnHome(@Param("date") LocalDateTime date);

}
