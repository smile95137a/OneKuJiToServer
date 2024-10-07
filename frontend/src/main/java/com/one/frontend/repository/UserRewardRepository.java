package com.one.frontend.repository;

import com.one.frontend.model.UserReward;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserRewardRepository {

    // 保存用户奖励
    @Insert("INSERT INTO user_reward (user_id, reward_amount, reward_date, created_at) " +
            "VALUES (#{userId}, #{rewardAmount}, #{rewardDate}, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(UserReward userReward);

    // 根据用户 ID 和时间范围检查用户当月是否已领取奖励
    @Select("SELECT COUNT(*) > 0 FROM user_reward " +
            "WHERE user_id = #{userId} AND reward_date BETWEEN #{startDate} AND #{endDate}")
    boolean hasReceivedRewardForMonth(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    // 根据 ID 查询用户奖励
    @Select("SELECT * FROM user_reward WHERE id = #{id}")
    UserReward findById(Long id);

    // 查询指定用户的所有奖励
    @Select("SELECT * FROM user_reward WHERE user_id = #{userId}")
    List<UserReward> findByUserId(Long userId);

    // 更新用户奖励
    @Update("UPDATE user_reward SET reward_amount = #{rewardAmount}, reward_date = #{rewardDate}, " +
            "created_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(UserReward userReward);

    // 删除用户奖励
    @Delete("DELETE FROM user_reward WHERE id = #{id}")
    void delete(Long id);
}