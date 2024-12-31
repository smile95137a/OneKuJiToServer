package com.one.repository;

import com.one.frontend.model.VerificationToken;
import org.apache.ibatis.annotations.*;

@Mapper
public interface VerificationTokenRepository {

    @Select("SELECT id, token, user_id, expiry_date FROM verification_token WHERE token = #{token}")
    VerificationToken findByToken(String token);

    @Insert("INSERT INTO verification_token (token, user_id, expiry_date) " +
            "VALUES (#{token}, #{userId}, #{expiryDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(VerificationToken verificationToken);

    @Delete("DELETE FROM verification_token WHERE token = #{token}")
    void delete(VerificationToken verificationToken);
}
