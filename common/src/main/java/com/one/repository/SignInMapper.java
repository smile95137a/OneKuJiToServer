package com.one.repository;

import com.one.frontend.model.SignIn;
import com.one.frontend.response.SignInRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SignInMapper {

    @Select("SELECT * FROM sign_in")
    List<SignIn> findAll();

    @Select("SELECT * FROM sign_in")
    List<SignInRes> findAllByRes();

}
