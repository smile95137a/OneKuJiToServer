package com.one.frontend.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.one.frontend.model.StoreKeyword;

@Mapper
public interface StoreKeywordRepository {

	@Select("SELECT * FROM store_keyword WHERE keyword = #{keyword}")
	StoreKeyword findByKeyword(@Param("keyword") String keyword);

	@Insert("INSERT INTO store_keyword (keyword) VALUES (#{keyword})")
	void save(StoreKeyword storeKeyword);

	@Select("SELECT id FROM store_keyword WHERE keyword = #{keyword}")
	Long findIdByKeyword(@Param("keyword") String keyword);

}
