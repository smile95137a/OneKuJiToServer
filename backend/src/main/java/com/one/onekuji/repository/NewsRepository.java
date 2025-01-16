package com.one.onekuji.repository;

import com.one.onekuji.model.News;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewsRepository {

    // 查询所有新闻
    @Select("SELECT * FROM news")
    List<News> getAllNews();

    // 根据ID查询新闻
    @Select("SELECT * FROM news WHERE news_uid = #{newsUid}")
    News getNewsById(String newsUid);

    // 新增新闻
    @Insert("INSERT INTO news (news_uid , title, preview,content, created_date, image_urls, status,author ,updated_date , start_date , end_date , is_display_on_home) " +
            "VALUES (#{newsUid} , #{title}, #{preview},#{content}, #{createdDate}, #{imageUrls}, #{status} , #{author} , #{updatedDate} , #{startDate} , #{endDate} , #{isDisplayOnHome})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertNews(News news);

    // 更新新闻
    @Update("UPDATE news SET title = #{title}, preview = #{preview}, created_date = #{createdDate}, " +
            "image_urls = #{imageUrls},content = #{content}, status = #{status} , title = #{title} , start_date = #{startDate} , end_date = #{end_date} , is_display_on_home = #{#{isDisplayOnHome}} WHERE news_uid = #{newsUid}")
    int updateNews(News news);

    // 删除新闻
    @Delete("DELETE FROM news WHERE  news_uid = #{newsUid}")
    int deleteNews(String newsUid);
}
