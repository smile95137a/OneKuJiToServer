package com.one.frontend.repository;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.one.frontend.model.DailySignInRecord;

@Mapper
public interface DailySignInRepository {

	@Select("SELECT * FROM daily_sign_in_records WHERE user_id = #{userId}")
	List<DailySignInRecord> getRecordsByUserId(Long userId);

	@Select("SELECT * FROM daily_sign_in_records WHERE user_id = #{userId} AND sign_in_date = #{signInDate}")
	DailySignInRecord getRecordByUserIdAndDate(@Param("userId") Long userId, @Param("signInDate") LocalDate signInDate);

	@Insert("INSERT INTO daily_sign_in_records (user_id, sign_in_date, reward_points) VALUES (#{userId}, #{signInDate}, #{rewardPoints})")
	void insertSignInRecord(DailySignInRecord record);

	@Delete("DELETE FROM daily_sign_in_records WHERE id = #{id}")
	int deleteSignInRecord(Long id);

}
