package com.one.repository;

import com.one.frontend.response.ShippingMethodRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ShippingMethodRepository {
    @Select("SELECT * FROM shipping_method WHERE #{size} BETWEEN min_size AND max_size ORDER BY min_size")
    List<ShippingMethodRes> findShippingMethodBySize(@Param("size") BigDecimal size);

    @Select("select shipping_price from shipping_method where code = #{code}")
    BigDecimal getShippingPrice(String code);
}
