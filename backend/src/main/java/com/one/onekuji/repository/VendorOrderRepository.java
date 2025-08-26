package com.one.onekuji.repository;

import com.one.onekuji.model.VendorOrderEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VendorOrderRepository {

    @Insert("INSERT INTO vendor_order (vendor_order, order_no, error_code, error_message , express , status) " +
            "VALUES (#{vendorOrder}, #{orderNo}, #{errorCode}, #{errorMessage} , #{express} , #{status})")
    void insert(VendorOrderEntity vendorOrder);

    @Select("SELECT * FROM vendor_order WHERE vendor_order = #{vendorOrder}")
    VendorOrderEntity findById(String vendorOrder);

    @Select("SELECT * FROM vendor_order")
    List<VendorOrderEntity> findAll();

    @Update("UPDATE vendor_order SET status = #{vendorOrder.status} WHERE vendor_order = #{vendorOrder.vendorOrder}")
    void update(@Param("vendorOrder") VendorOrderEntity vendorOrder);

    @Delete("DELETE FROM vendor_order WHERE vendorOrder = #{vendorOrder}")
    void delete(String vendorOrder);

    @Select("SELECT * FROM vendor_order WHERE vendor_order = #{vendorOrder}")
    VendorOrderEntity findByVendorOrder(String vendorOrder);

    @Insert("INSERT INTO vendor_order (vendor_order, order_no, error_code, error_message, express, status) " +
            "VALUES (#{vendorOrder}, #{orderNo}, #{errorCode}, #{errorMessage}, #{express}, #{status})")
    void insert2(VendorOrderEntity vendorOrderEntity);

    @Update("UPDATE vendor_order SET order_no = #{orderNo}, error_code = #{errorCode}, error_message = #{errorMessage}, " +
            "express = #{express}, status = #{status} WHERE vendor_order = #{vendorOrder}")
    void update2(VendorOrderEntity vendorOrderEntity);
}
