package com.one.frontend.repository;

import com.one.frontend.model.OrderDetail;
import com.one.frontend.model.OrderDetailTemp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDetailTempMapper {

    @Select("SELECT * FROM order_detail_temp WHERE id = #{id}")
    OrderDetailTemp getOrderDetailById(Long id);

    @Select("SELECT * FROM order_detail_temp WHERE order_id = #{orderId}")
    List<OrderDetailTemp> getOrderDetailsByOrderId(Long orderId);

    @Insert("INSERT INTO order_detail_temp (order_id, product_detail_id, store_product_id, quantity, unit_price, result_item_id, bonus_points_earned, total_price) " +
            "VALUES (#{orderId}, #{productDetailId}, #{storeProductId}, #{quantity}, #{unitPrice}, #{resultItemId}, #{bonusPointsEarned}, #{totalPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOrderDetail(OrderDetail orderDetail);

    @Update("UPDATE order_detail_temp SET product_detail_id = #{productDetailId}, store_product_id = #{storeProductId}, quantity = #{quantity}, unit_price = #{unitPrice}, result_item_id = #{resultItemId}, bonus_points_earned = #{bonusPointsEarned}, total_price = #{totalPrice} WHERE id = #{id}")
    void updateOrderDetail(OrderDetailTemp orderDetail);

    @Delete("DELETE FROM order_detail_temp WHERE order_id = #{id}")
    void deleteOrderDetail(Long id);
}
