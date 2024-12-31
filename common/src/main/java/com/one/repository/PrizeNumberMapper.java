package com.one.repository;

import com.one.frontend.model.PrizeNumber;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PrizeNumberMapper {

    @Select("select a.product_id ,  a.product_detail_id , a.prize_number_id , a.number , a.is_drawn , a.level from prize_number a join product_detail b on a.product_detail_id = b.product_detail_id\n" +
            "where a.product_detail_id = #{productDetailId} " +
            "order by  CAST(a.number AS UNSIGNED)")
    List<PrizeNumber> getAllPrizeNumbersByProductDetailId(Long productDetailId);

    @Select("SELECT * FROM prize_number WHERE product_id = #{productId} AND is_drawn = FALSE")
    List<PrizeNumber> getAvailablePrizeNumbersByProductDetailId(Long productId);

    @Update("UPDATE prize_number SET is_drawn = TRUE WHERE prize_number_id = #{prizeNumberId} and product_id = #{productId} and product_detail_id = #{productDetailId}")
    void markPrizeNumberAsDrawn(Long prizeNumberId , Long productId , Long productDetailId);

    @Select("select `number` from prize_number where product_id = #{productId} and is_drawn = false;")
    List<Long> getNumbers(Long productId);

    @Select({
            "<script>",
            "SELECT * FROM prize_number",
            "WHERE product_id = #{productId}",
            "AND number IN",
            "<foreach item='number' collection='numbers' open='(' separator=',' close=')'>",
            "#{number}",
            "</foreach>",
            "</script>"
    })
    List<PrizeNumber> getPrizeNumbersByProductIdAndNumbers(
            @Param("productId") Long productId,
            @Param("numbers") List<String> numbers
    );
    @Update("UPDATE prize_number SET is_drawn = #{isDrawn} , level = #{level} WHERE number = #{number} and product_id = #{productId} and product_detail_id = #{productDetailId}")
    void updatePrizeNumber(PrizeNumber prizeNumber);
    @Update({
            "<script>",
            "UPDATE prize_number",
            "SET is_drawn = 1,",  // 直接设置为 true (1)
            "level = CASE number",
            "<foreach collection='list' item='prizeNumber' index='index'>",
            "WHEN #{prizeNumber.number} THEN #{prizeNumber.level} ",
            "</foreach>",
            "ELSE level END,",
            "product_detail_id = CASE number",
            "<foreach collection='list' item='prizeNumber' index='index'>",
            "WHEN #{prizeNumber.number} THEN #{prizeNumber.productDetailId} ",
            "</foreach>",
            "ELSE product_detail_id END",
            "WHERE number IN",
            "<foreach collection='list' item='prizeNumber' index='index' open='(' separator=',' close=')'>",
            "#{prizeNumber.number}",
            "</foreach>",
            "AND product_id = #{productId}",
            "</script>"
    })
    void updatePrizeNumberBatch(@Param("list") List<PrizeNumber> prizeNumbers, @Param("productId") Long productId);

    @Select("select count(*) from prize_number where product_id = #{productId}")
    int getNumbersCount(Long productId);
    @Select("select count(*) from prize_number where product_id = #{productId}  and is_drawn = true")
    int getNumbersCountIsTrue(Long productId);
}