package com.one.frontend.repository;

import com.one.frontend.model.RedemptionCode;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface RedemptionCodeMapper {

    @Insert("INSERT INTO redemption_codes (code, is_redeemed, user_id) VALUES (#{code}, #{isRedeemed}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRedemptionCode(RedemptionCode redemptionCode);

    @Select("SELECT * FROM redemption_codes WHERE code = #{code}")
    Optional<RedemptionCode> findByCode(String code);

        @Update("UPDATE redemption_codes SET is_redeemed = 1, redeemed_at = #{redeemedAt}, user_id = #{userId} WHERE id = #{id}")
        void updateRedemptionCode(RedemptionCode redemptionCode);

    @Select("SELECT * FROM redemption_codes WHERE id = #{id}")
    Optional<RedemptionCode> findById(Long id);
}
