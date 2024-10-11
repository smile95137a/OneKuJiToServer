package com.one.onekuji.repository;

import com.one.onekuji.model.VendorOrderEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VendorOrderRepository {

    @Insert("INSERT INTO vendor_order (vendor_order, order_no, error_code, error_message , express , status) " +
            "VALUES (#{vendorOrder}, #{orderNo}, #{errorCode}, #{errorMessage} , #{express} , #{status})")
    void insert(VendorOrderEntity vendorOrder);

    @Select("SELECT * FROM vendor_order WHERE vendorOrder = #{vendorOrder}")
    VendorOrderEntity findById(String vendorOrder);

    @Select("SELECT * FROM vendor_order")
    List<VendorOrderEntity> findAll();

    @Update("UPDATE vendor_order SET status = #{vendorOrder.status} WHERE vendor_order = #{vendorOrder.vendorOrder}")
    void update(@Param("vendorOrder") VendorOrderEntity vendorOrder);

    @Delete("DELETE FROM vendor_order WHERE vendorOrder = #{vendorOrder}")
    void delete(String vendorOrder);
}
