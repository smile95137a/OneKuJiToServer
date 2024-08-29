package com.one.onekuji.repository;

import com.one.onekuji.model.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderRepository {
    @Insert("INSERT INTO `order` (order_number, user_id, total_amount, bonus_points_earned, bonus_points_used, status, payment_method, payment_status, created_at, updated_at, paid_at, notes) " +
            "VALUES (#{orderNumber}, #{userId}, #{totalAmount}, #{bonusPointsEarned}, #{bonusPointsUsed}, #{status}, #{paymentMethod}, #{paymentStatus}, #{createdAt}, #{updatedAt}, #{paidAt}, #{notes})")
    void insertOrder(Order order);

    @Select("select id from `order` where order_number = #{orderNumber}")
    Long getOrderIdByOrderNumber(String orderNumber);

    @Select("SELECT * FROM `order`  WHERE id = #{id}")
    Order getOrderById(Long id);

    @Select("SELECT * FROM `order` ")
    List<Order> getAllOrders();

    @Update("UPDATE `order` SET status = #{order.status}, updated_at = #{order.updatedAt}, notes = #{order.notes} WHERE id = #{id}")
    void updateOrder(@Param("id") Long id ,@Param("order")Order order);

    @Delete("DELETE FROM `order`  WHERE id = #{id}")
    void deleteOrder(Long id);
}
