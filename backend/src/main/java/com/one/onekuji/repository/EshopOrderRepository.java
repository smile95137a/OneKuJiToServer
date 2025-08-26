package com.one.onekuji.repository;

import com.one.onekuji.model.EshopOrderEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EshopOrderRepository {

    @Insert("INSERT INTO eshop_order (eshopId, errorCode, errorMessage) " +
            "VALUES (#{eshopId}, #{errorCode}, #{errorMessage})")
    void insert(EshopOrderEntity eshopOrder);

    @Select("SELECT * FROM eshop_order WHERE eshopId = #{eshopId}")
    EshopOrderEntity findById(String eshopId);

    @Select("SELECT * FROM eshop_order")
    List<EshopOrderEntity> findAll();

    @Update("UPDATE eshop_order SET errorCode = #{errorCode}, errorMessage = #{errorMessage} " +
            "WHERE eshopId = #{eshopId}")
    void update(EshopOrderEntity eshopOrder);

    @Delete("DELETE FROM eshop_order WHERE eshopId = #{eshopId}")
    void delete(String eshopId);
}
