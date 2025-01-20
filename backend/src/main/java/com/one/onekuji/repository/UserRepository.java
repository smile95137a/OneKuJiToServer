package com.one.onekuji.repository;

import com.one.onekuji.model.User;
import com.one.onekuji.response.UserRes;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface UserRepository {

    @Select("SELECT * FROM user")
    List<UserRes> findAll();

    @Select("SELECT id, username, password, nickname, email, phone_number, address, role_id, balance, bonus, sliver_coin, updated_at, draw_count FROM user WHERE id = #{userId}")
    UserRes findById(Long userId);

    // 插入新用戶
    @Insert("INSERT INTO user (username, password, nickname, email, phone_number, address, role_id, balance, created_at , bonus , sliver_coin) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, #{phoneNumber}, #{address}, #{roleId}, #{balance}, #{createdAt} , #{bonus} , #{sliverCoin})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(User user);

    // 更新用戶
    @Update("UPDATE user SET username = #{username}, password = #{password}, nickname = #{nickname}, email = #{email}, phone_number = #{phoneNumber}, address = #{address}, " +
            "role_id = #{roleId}, balance = #{balance}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void update(User user);


    @Delete("DELETE FROM user WHERE id = #{userId}")
    void delete(Long userId);

    @Select("select * from user where username = #{username}")
    User getUserByUserName(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById2(Long id);


    @Update({
            "<script>",
            "UPDATE `user`",
            "SET sliver_coin = sliver_coin + #{sliverCoin},",
            "bonus = bonus + #{bonus} , balance = balance + #{balance}",  // 新增对 bonus 字段的更新
            "WHERE id IN",
            "<foreach item='userId' collection='userIdList' open='(' separator=',' close=')'>",
            "#{userId}",
            "</foreach>",
            "</script>"
    })
    void updateSliverCoinBatch(@Param("userIdList") List<Long> userIdList, @Param("sliverCoin") BigDecimal sliverCoin, @Param("bonus") BigDecimal bonus , @Param("balance") BigDecimal balance);


    @Insert({
            "<script>",
            "INSERT INTO user_update_log (user_ids, sliver_coin_delta, bonus_delta , balance)",
            "VALUES",
            "<foreach item='userId' collection='userIdList' separator=','>",
            "(#{userId}, #{sliverCoin}, #{bonus} , #{balance})",
            "</foreach>",
            "</script>"
    })
    void logUpdate(@Param("userIdList") List<Long> userIdList, @Param("sliverCoin") BigDecimal sliverCoin, @Param("bonus") BigDecimal bonus , @Param("balance") BigDecimal balance);
}
