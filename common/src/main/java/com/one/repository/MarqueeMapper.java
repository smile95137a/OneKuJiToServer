package com.one.repository;

import com.one.model.Marquee;
import com.one.model.MarqueeDetail;
import com.one.response.MarqueeWithDetailsRes;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MarqueeMapper {

	@Insert("INSERT INTO `marquee` (user_id, create_date) " +
	        "VALUES (#{userId}, #{createDate})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void addMarquee(Marquee marquee);


	@Insert("INSERT INTO `marquee_detail` (marquee_id, grade, name) " +
	        "VALUES (#{marqueeId}, #{grade}, #{name})")
	void addMarqueeDetail(MarqueeDetail marqueeDetail);
	
    @Select("""
        SELECT 
            m.id AS marqueeId,
            m.user_id AS userId,
            u.nickname as username,
            m.create_date AS createDate,
            d.grade AS grade,
            d.name AS name
        FROM marquee m
        LEFT JOIN user u ON m.user_id = u.id -- 加入用戶表
        LEFT JOIN marquee_detail d ON m.id = d.marquee_id
    """)
    List<MarqueeWithDetailsRes> findAllWithDetailsAndUser();
}
