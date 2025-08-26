package com.one.repository;

import com.one.model.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NewsRepository {

    // 查询所有新闻
    @Select("SELECT * FROM news where status = 'AVAILABLE' order by created_date desc")
    List<News> getAllNews();

    // 根据ID查询新闻
    @Select("SELECT * FROM news WHERE news_uid = #{newsUid}")
    News getNewsById(String newsUid);

}
