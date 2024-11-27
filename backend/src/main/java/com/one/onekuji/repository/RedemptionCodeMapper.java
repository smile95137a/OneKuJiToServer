package com.one.onekuji.repository;

import com.one.onekuji.model.RedemptionCode;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RedemptionCodeMapper {

    @Insert({
            "<script>",
            "INSERT INTO redemption_codes (code, is_redeemed, redeemed_at, product_id) VALUES ",
            "<foreach collection='redemptionCodes' item='code' separator=','>",
            "(#{code.code}, #{code.isRedeemed}, #{code.redeemedAt}, #{code.productId})",
            "</foreach>",
            "</script>"
    })
    void insertRedemptionCodes(@Param("redemptionCodes") List<RedemptionCode> redemptionCodes);


    @Select("SELECT * FROM redemption_codes WHERE code = #{code}")
    Optional<RedemptionCode> findByCode(String code);

    @Update("UPDATE redemption_codes SET is_redeemed = #{isRedeemed}, redeemed_at = #{redeemedAt}, user_id = #{userId} WHERE id = #{id}")
    void updateRedemptionCode(RedemptionCode redemptionCode);

    @Select("SELECT * , p.product_name as productName FROM redemption_codes rc left join product p on rc.product_id = p.product_id where p.product_type = 'CUSTMER_PRIZE' order by p.product_id desc")
    List<RedemptionCode> findById();

    @Select("SELECT * , p.product_name as productName FROM redemption_codes rc left join product p on rc.product_id = p.product_id where p.product_type = 'CUSTMER_PRIZE' and p.product_id = #{productId} order by p.product_id desc")
    List<RedemptionCode> findByProductId(Long productId);
}
