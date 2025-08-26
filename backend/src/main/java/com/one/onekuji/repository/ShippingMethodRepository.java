package com.one.onekuji.repository;

import com.one.onekuji.request.ShippingMethodReq;
import com.one.onekuji.response.ShippingMethodRes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShippingMethodRepository {

    @Select("SELECT * FROM shipping_method")
    List<ShippingMethodRes> findAll();

    @Select("SELECT * FROM shipping_method WHERE shipping_method_id = #{id}")
    ShippingMethodRes findById(Long id);

    @Insert("INSERT INTO shipping_method(name, min_size, max_size, shipping_price, create_date, update_date , code) " +
            "VALUES(#{name}, #{minSize}, #{maxSize}, #{shippingPrice}, #{createDate}, #{updateDate} , #{code})")
    void insert(ShippingMethodReq shippingMethodReq);

    @Update("UPDATE shipping_method SET name = #{name}, min_size = #{minSize}, max_size = #{maxSize}, " +
            "shipping_price = #{shippingPrice}, update_date = #{updateDate} WHERE shipping_method_id = #{shippingMethodId}")
    void update(ShippingMethodRes shippingMethodRes);

    @Delete("DELETE FROM shipping_method WHERE shipping_method_id = #{id}")
    void delete(Long id);
}
