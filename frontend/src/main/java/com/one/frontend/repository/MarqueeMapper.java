package com.one.frontend.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.one.frontend.model.Marquee;
import com.one.frontend.model.MarqueeDetail;
import com.one.frontend.response.MarqueeWithDetailsRes;

@Mapper
public interface MarqueeMapper {

	@Insert("INSERT INTO `marquee` (user_id, create_date) " +
	        "VALUES (#{userId}, #{createDate}) RETURNING id")
	Long addMarqueeAndReturnId(Marquee marquee);

	@Insert("INSERT INTO `marquee_detail` (marquee_id, grade, name) " +
	        "VALUES (#{marqueeId}, #{grade}, #{name})")
	void addMarqueeDetail(MarqueeDetail marqueeDetail);
	
    @Select("""
        SELECT 
            m.id AS marqueeId,
            m.user_id AS userId,
            u.username,
            m.create_date AS createDate,
            d.grade AS grade,
            d.name AS name
        FROM marquee m
        LEFT JOIN user u ON m.user_id = u.id -- 加入用戶表
        LEFT JOIN marquee_detail d ON m.id = d.marquee_id
    """)
    List<MarqueeWithDetailsRes> findAllWithDetailsAndUser();
}
