package com.one.frontend.repository;

import com.one.frontend.model.DrawResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DrawRepository {

    @Insert("<script>" +
            "INSERT INTO draw_result (user_id, product_id, product_detail_id, draw_time, status, amount, draw_count, create_date, update_date, total_draw_count, remaining_draw_count, image_urls, product_name , sliver_price , price , bonus_price , pay_type) " +
            "VALUES " +
            "<foreach collection='drawResults' item='drawResult' index='index' separator=','>" +
            "(#{drawResult.userId}, #{drawResult.productId}, #{drawResult.productDetailId}, #{drawResult.drawTime}, #{drawResult.status}, #{drawResult.amount}, #{drawResult.drawCount}, #{drawResult.createDate}, #{drawResult.updateDate}, #{drawResult.totalDrawCount}, #{drawResult.remainingDrawCount}, #{drawResult.imageUrls}, #{drawResult.productName} , #{drawResult.sliverPrice} , #{drawResult.price} , #{drawResult.bonusPrice} , #{drawResult.payType})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("drawResults") List<DrawResult> drawResults);


    @Insert("INSERT INTO draw_result (user_id, product_id, product_detail_id, draw_time, amount, draw_count, remaining_draw_count, prize_number, status, create_date, update_date) " +
            "VALUES (#{userId}, #{productId}, #{productDetailId}, #{drawTime}, #{amount}, #{drawCount}, #{remainingDrawCount}, #{prizeNumber}, #{status}, #{createDate}, #{updateDate})")
    void insertDrawResult(DrawResult drawResult);


    @Insert("<script>" +
            "INSERT INTO draw_result (user_id, product_id, product_detail_id, draw_time, status, amount, draw_count, create_date, update_date, total_draw_count, remaining_draw_count) " +
            "VALUES " +
            "<foreach collection='drawResults' item='drawResult' index='index' separator=','>" +
            "(#{drawResult.userId}, #{drawResult.productId}, #{drawResult.productDetailId}, #{drawResult.drawTime}, #{drawResult.status}, #{drawResult.amount}, #{drawResult.drawCount}, #{drawResult.createDate}, #{drawResult.updateDate}, #{drawResult.totalDrawCount}, #{drawResult.remainingDrawCount})" +
            "</foreach>" +
            "</script>")
    void insertBatchforGACHA(@Param("drawResults") List<DrawResult> drawResults);


}
