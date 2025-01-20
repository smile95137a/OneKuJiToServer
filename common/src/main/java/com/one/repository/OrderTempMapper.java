package com.one.repository;

import com.one.model.Order;
import com.one.model.OrderTemp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderTempMapper {

    @Insert("INSERT INTO order_temp(order_number, user_id, total_amount, shipping_cost, is_free_shipping, " +
            "bonus_points_earned, bonus_points_used, created_at, updated_at, paid_at, result_status, payment_method, " +
            "shipping_method, shipping_name, shipping_email, shipping_phone, shipping_zip_code, shipping_city, " +
            "shipping_area, shipping_address, billing_zip_code, billing_name, billing_email, billing_phone, " +
            "billing_city, billing_area, billing_address, invoice, tracking_number, cart_item_id, express, shop_id, " +
            "OPMode) " +
            "VALUES (#{orderNumber}, #{userId}, #{totalAmount}, #{shippingCost}, #{isFreeShipping}, " +
            "#{bonusPointsEarned}, #{bonusPointsUsed}, #{createdAt}, #{updatedAt}, #{paidAt}, #{resultStatus}, " +
            "#{paymentMethod}, #{shippingMethod}, #{shippingName}, #{shippingEmail}, #{shippingPhone}, " +
            "#{shippingZipCode}, #{shippingCity}, #{shippingArea}, #{shippingAddress}, #{billingZipCode}, #{billingName}, " +
            "#{billingEmail}, #{billingPhone}, #{billingCity}, #{billingArea}, #{billingAddress}, #{invoice}, " +
            "#{trackingNumber}, #{cartItemIds}, #{express}, #{shopId}, #{OPMode})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addOrderTemp(Order orderTemp);

    @Select("SELECT * FROM order_temp WHERE id = #{id}")
    OrderTemp getOrderTempById(Integer id);

    @Select("SELECT * FROM order_temp")
    List<OrderTemp> getAllOrderTemps();

    @Update("UPDATE order_temp SET order_number = #{orderNumber}, user_id = #{userId}, total_amount = #{totalAmount}, " +
            "shipping_cost = #{shippingCost}, is_free_shipping = #{isFreeShipping}, bonus_points_earned = #{bonusPointsEarned}, " +
            "bonus_points_used = #{bonusPointsUsed}, updated_at = #{updatedAt}, paid_at = #{paidAt}, result_status = #{resultStatus}, " +
            "payment_method = #{paymentMethod}, shipping_method = #{shippingMethod}, shipping_name = #{shippingName}, " +
            "shipping_email = #{shippingEmail}, shipping_phone = #{shippingPhone}, shipping_zip_code = #{shippingZipCode}, " +
            "shipping_city = #{shippingCity}, shipping_area = #{shippingArea}, shipping_address = #{shippingAddress}, " +
            "billing_zip_code = #{billingZipCode}, billing_name = #{billingName}, billing_email = #{billingEmail}, " +
            "billing_phone = #{billingPhone}, billing_city = #{billingCity}, billing_area = #{billingArea}, " +
            "billing_address = #{billingAddress}, invoice = #{invoice}, tracking_number = #{trackingNumber}, " +
            "cart_item_id = #{cartItemIds}, express = #{express}, shop_id = #{shopId}, OPMode = #{OPMode} " +
            "WHERE id = #{id}")
    void updateOrderTemp(OrderTemp orderTemp);

    @Delete("DELETE FROM order_temp WHERE id = #{id}")
    void deleteOrderTemp(Integer id);
    @Select("SELECT id FROM `order_temp` WHERE order_number = #{orderNumber}")
    Long getOrderIdByOrderNumber(String orderNumber);
}
