package com.one.onekuji.repository;

import com.one.onekuji.model.PrizeNumber;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PrizeNumberMapper {

    // 插入奖品编号
    @Insert("INSERT INTO prizenumber (product_id, product_detail_id, number, is_drawn) VALUES (#{productId}, #{productDetailId}, #{number}, #{isDrawn})")
    void insertPrizeNumber(PrizeNumber prizeNumber);

    @Select("SELECT MAX(number) FROM prizenumber WHERE product_id = #{productId}")
    Integer getMaxPrizeNumberByProductId(Long productId);
}
