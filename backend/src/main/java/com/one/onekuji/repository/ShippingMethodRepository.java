package com.one.onekuji.repository;

import com.one.onekuji.response.ShippingMethodRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ShippingMethodRepository {
    @Select("SELECT * FROM shipping_method WHERE #{size} BETWEEN min_size AND max_size ORDER BY min_size")
    List<ShippingMethodRes> findShippingMethodBySize(@Param("size") BigDecimal size);
}
