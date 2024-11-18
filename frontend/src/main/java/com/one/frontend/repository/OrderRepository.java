package com.one.frontend.repository;

import com.one.frontend.dto.DrawResultDto;
import com.one.frontend.model.Order;
import com.one.frontend.response.OrderRes;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderRepository {

	@Insert("INSERT INTO `order` (order_number, user_id, total_amount, shipping_cost, is_free_shipping, "
			+ "bonus_points_earned, bonus_points_used, created_at, updated_at, paid_at, result_status, "
			+ "payment_method, shipping_method, shipping_name, shipping_zip_code, shipping_city, shipping_area, "
			+ "shipping_address, billing_zip_code, billing_name, billing_city, billing_area, "
			+ "billing_address, invoice, tracking_number , shipping_phone , shop_id , OPMode , e_payaccount , bill_number , state , donation_code , type , shipping_email , shop_name , shop_address) "
			+ "VALUES (#{orderNumber}, #{userId}, #{totalAmount}, #{shippingCost}, #{isFreeShipping}, "
			+ "#{bonusPointsEarned}, #{bonusPointsUsed}, #{createdAt}, #{updatedAt}, #{paidAt}, #{resultStatus}, "
			+ "#{paymentMethod}, #{shippingMethod}, #{shippingName}, #{shippingZipCode}, #{shippingCity}, #{shippingArea}, "
			+ "#{shippingAddress}, #{billingZipCode}, #{billingName}, #{billingCity}, #{billingArea}, "
			+ "#{billingAddress}, #{invoice}, #{trackingNumber} , #{shippingPhone} , #{shopId} , #{OPMode} , #{ePayAccount} , #{billNumber} , #{state} , #{donationCode} , #{type} , #{shippingEmail} , #{shopName} , #{shopAddress})")
	void insertOrder(Order order);

	@Select("SELECT id FROM `order` WHERE order_number = #{orderNumber}")
	Long getOrderIdByOrderNumber(String orderNumber);

	@Select("SELECT * FROM `order` WHERE user_id = #{userId}")
	Order getOrderByUserId(Long userId);

	@Select("SELECT * FROM `order` WHERE user_id = #{userId} AND order_number = #{orderNumber}")
	OrderRes getOrderByUserIdAndOrderNumber(Long userId, String orderNumber);

	@Results(id = "orderResultMap", value = { 
			@Result(property = "id", column = "id"),
			@Result(property = "orderNumber", column = "order_number"),
			@Result(property = "totalAmount", column = "total_amount"),
			@Result(property = "bonusPointsEarned", column = "bonus_points_earned"),
			@Result(property = "bonusPointsUsed", column = "bonus_points_used"),
			@Result(property = "resultStatus", column = "result_status"),
			@Result(property = "createdAt", column = "created_at"), })
	@Select("SELECT o.* FROM `order` o WHERE o.order_number = #{orderNumber}")
	OrderRes findOrderByOrderNumber(String orderNumber);

	static String buildFindOrdersByDateRange(Map<String, Object> params) {
		Long userId = (Long) params.get("userId");
		LocalDateTime startDate = (LocalDateTime) params.get("startDate");
		LocalDateTime endDate = (LocalDateTime) params.get("endDate");

		var sql = new SQL() {
			{
				SELECT("*");
				FROM("`order`");
				if (userId != null) {
					WHERE("user_id = #{userId}");
				}
				if (startDate != null) {
					WHERE("created_at >= #{startDate}");
				}
				if (endDate != null) {
					WHERE("created_at <= #{endDate}");
				}
			}
		}.toString();

		return sql;
	}

	@SelectProvider(type = OrderRepository.class, method = "buildFindOrdersByDateRange")
	@ResultMap("orderResultMap")
	List<OrderRes> findOrdersByDateRange(Map<String, Object> params);

	@Select("<script>"
			+ "SELECT a.*, b.product_name as productName , b.image_urls as imageUrls "
			+ "FROM draw_result a "
			+ "LEFT JOIN product_detail b ON a.product_detail_id = b.product_detail_id "
			+ "WHERE 1=1 "
			+ "<if test='userId != null'> AND a.user_id = #{userId} </if>"
			+ "<if test='startDate != null'> AND a.draw_time &gt;= #{startDate} </if>"
			+ "<if test='endDate != null'> AND a.draw_time &lt;= #{endDate} </if>"
			+ "ORDER BY a.draw_time DESC"
			+ "</script>")
	List<DrawResultDto> queryDrawOrder(@Param("userId") Object userId,
									   @Param("startDate") Object startDate,
									   @Param("endDate") Object endDate);
	@Update("UPDATE `order` SET result_status = 'PREPARING_SHIPMENT' WHERE id = #{orderNumber}")
	void updateStatus(@Param("orderNumber") Long orderNumber);
	@Update("UPDATE `order` SET result_status = 'FAILED_PAYMENT' WHERE id = #{orderNumber}")
	void updateStatusByFail(@Param("orderNumber") Long orderNumber);
}
