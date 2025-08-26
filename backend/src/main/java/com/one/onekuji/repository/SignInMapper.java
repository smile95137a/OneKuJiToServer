package com.one.onekuji.repository;

import com.one.onekuji.model.SignIn;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SignInMapper {

    @Select("SELECT * FROM sign_in WHERE id = #{id}")
    SignIn findById(Long id);

    @Select("SELECT * FROM sign_in")
    List<SignIn> findAll();

    @Insert("INSERT INTO sign_in (sliver_price, probability, number, created_date, update_date) " +
            "VALUES (#{sliverPrice}, #{probability}, #{number}, #{createdDate}, #{updateDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SignIn signIn);

    @Update("UPDATE sign_in SET sliver_price = #{sliverPrice}, probability = #{probability}, number = #{number}, " +
            "update_date = #{updateDate} WHERE id = #{id}")
    int update(SignIn signIn);

    @Delete("DELETE FROM sign_in WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT COUNT(*) FROM sign_in")
    int countAll();
}
