package com.one.onekuji.repository;

import com.one.onekuji.model.CustomerOrderEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CustomerOrderRepository {

    @Insert("INSERT INTO customer_order (vendorOrder, orderNo, errorCode, errorMessage) " +
            "VALUES (#{vendorOrder}, #{orderNo}, #{errorCode}, #{errorMessage})")
    void insert(CustomerOrderEntity customerOrder);

    @Select("SELECT * FROM customer_order WHERE vendorOrder = #{vendorOrder}")
    CustomerOrderEntity findById(String vendorOrder);

    @Update("UPDATE customer_order SET orderNo = #{orderNo}, errorCode = #{errorCode}, errorMessage = #{errorMessage} " +
            "WHERE vendorOrder = #{vendorOrder}")
    void update(CustomerOrderEntity customerOrder);

    @Delete("DELETE FROM customer_order WHERE vendorOrder = #{vendorOrder}")
    void delete(String vendorOrder);
}
