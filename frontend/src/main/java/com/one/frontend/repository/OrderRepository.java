package com.one.frontend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import com.one.frontend.dto.OrderDto;
import com.one.frontend.model.Order;
import com.one.frontend.response.OrderRes;

@Mapper
public interface OrderRepository {

	@Insert("INSERT INTO `order` (order_number, user_id, total_amount, shipping_cost, is_free_shipping, "
			+ "bonus_points_earned, bonus_points_used, created_at, updated_at, paid_at, result_status, "
			+ "payment_method, shipping_method, shipping_name, shipping_zip_code, shipping_city, shipping_area, "
			+ "shipping_address, billing_zip_code, billing_name, billing_city, billing_area, "
			+ "billing_address, invoice, tracking_number) "
			+ "VALUES (#{orderNumber}, #{userId}, #{totalAmount}, #{shippingCost}, #{isFreeShipping}, "
			+ "#{bonusPointsEarned}, #{bonusPointsUsed}, #{createdAt}, #{updatedAt}, #{paidAt}, #{resultStatus}, "
			+ "#{paymentMethod}, #{shippingMethod}, #{shippingName}, #{shippingZipCode}, #{shippingCity}, #{shippingArea}, "
			+ "#{shippingAddress}, #{billingZipCode}, #{billingName}, #{billingCity}, #{billingArea}, "
			+ "#{billingAddress}, #{invoice}, #{trackingNumber})")
	void insertOrder(Order order);

	@Select("SELECT id FROM `order` WHERE order_number = #{orderNumber}")
	Long getOrderIdByOrderNumber(String orderNumber);

	@Select("SELECT * FROM `order` WHERE user_id = #{userId}")
	Order getOrderByUserId(Long userId);

	@Select("SELECT * FROM `order` WHERE user_id = #{userId} AND order_number = #{orderNumber}")
	OrderRes getOrderByUserIdAndOrderNumber(Long userId, String orderNumber);

}
