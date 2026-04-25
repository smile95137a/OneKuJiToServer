package com.one.onekuji.repository;

import com.one.onekuji.model.Order;
import com.one.onekuji.request.OrderQueryReq;
import com.one.onekuji.response.OrderRes;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderRepository {
    @Insert("INSERT INTO `order` (order_number, user_id, total_amount, bonus_points_earned, bonus_points_used, status, payment_method, payment_status, created_at, updated_at, paid_at, notes) " +
            "VALUES (#{orderNumber}, #{userId}, #{totalAmount}, #{bonusPointsEarned}, #{bonusPointsUsed}, #{status}, #{paymentMethod}, #{paymentStatus}, #{createdAt}, #{updatedAt}, #{paidAt}, #{notes})")
    void insertOrder(Order order);

    @Select("select id from `order` where order_number = #{orderNumber}")
    Long getOrderIdByOrderNumber(String orderNumber);

    @Select("SELECT * FROM `order`  WHERE id = #{id}")
    Order getOrderById(Long id);

    @Select("SELECT * FROM `order` WHERE id = #{id}")
    @Results(id = "orderByIdResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "orderNumber", column = "order_number"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "shippingCost", column = "shipping_cost"),
        @Result(property = "isFreeShipping", column = "is_free_shipping"),
        @Result(property = "bonusPointsEarned", column = "bonus_points_earned"),
        @Result(property = "bonusPointsUsed", column = "bonus_points_used"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "paidAt", column = "paid_at"),
        @Result(property = "resultStatus", column = "result_status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "shippingMethod", column = "shipping_method"),
        @Result(property = "shippingName", column = "shipping_name"),
        @Result(property = "shippingEmail", column = "shipping_email"),
        @Result(property = "shippingPhone", column = "shipping_phone"),
        @Result(property = "shippingZipCode", column = "shipping_zip_code"),
        @Result(property = "shippingCity", column = "shipping_city"),
        @Result(property = "shippingArea", column = "shipping_area"),
        @Result(property = "shippingAddress", column = "shipping_address"),
        @Result(property = "billingZipCode", column = "billing_zip_code"),
        @Result(property = "billingName", column = "billing_name"),
        @Result(property = "billingCity", column = "billing_city"),
        @Result(property = "billingArea", column = "billing_area"),
        @Result(property = "billingAddress", column = "billing_address"),
        @Result(property = "invoice", column = "invoice"),
        @Result(property = "trackingNumber", column = "tracking_number"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "OPMode", column = "OPMode"),
        @Result(property = "shippingMethodId", column = "shipping_mehtod_id"),
        @Result(property = "shopName", column = "shop_name"),
        @Result(property = "shopAddress", column = "shop_address"),
    })
    OrderRes getOrderResById(Long id);

    @Select("SELECT * FROM `order` ")
    List<Order> getAllOrders();

    @Update({
            "UPDATE `order`",
            "SET result_status = #{resultStatus},",
            "updated_at = #{now} " +
            "WHERE id = #{id}"
    })
    void updateOrder(@Param("id") Long id, @Param("resultStatus") String resultStatus , @Param("now") LocalDateTime now);

    @Delete("DELETE FROM `order`  WHERE id = #{id}")
    void deleteOrder(Long id);

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
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "shippingMethodId", column = "shipping_method_id"),
    })
    @Select("SELECT o.* FROM `order` o WHERE o.order_number = #{orderNumber}")
    OrderRes findOrderByOrderNumber(String orderNumber);

    static String buildFindOrdersByDateRange(Map<String, Object> params) {
        LocalDateTime startDate = (LocalDateTime) params.get("startDate");
        LocalDateTime endDate = (LocalDateTime) params.get("endDate");

        var sql = new SQL() {
            {
                SELECT("o.*, sm.shipping_method_id, sm.name"); // 這裡選擇了運輸方式的 ID 和名稱
                FROM("`order` o");
                LEFT_OUTER_JOIN("shipping_method sm ON o.shipping_method_id = sm.shipping_method_id"); // 假設 `order` 表有 `shipping_method_id` 字段
                if (startDate != null) {
                    WHERE("o.created_at >= #{startDate}");
                }
                if (endDate != null) {
                    WHERE("o.created_at <= #{endDate}");
                }
                // 使用正確的 SQL 語法 ORDER BY
                ORDER_BY("o.created_at DESC");  // 注意這裡的修正
            }
        }.toString();

        return sql;
    }


    @SelectProvider(type = OrderRepository.class, method = "buildFindOrdersByDateRange")
    @ResultMap("orderResultMap")
    List<OrderRes> findOrdersByDateRange(Map<String, Object> params);

    @Update("UPDATE `order` SET tracking_number = #{trackingNumber} WHERE id = #{orderId}")
    void updateTrackNumber(@Param("trackingNumber") String trackingNumber, @Param("orderId") Long orderId);

    @Select({
        "<script>",
        "SELECT o.*,",
        "  sm.shipping_method_id AS shippingMethodId,",
        "  sm.name AS shippingMethodName,",
        "  COUNT(od.id) AS orderCount",
        "FROM `order` o",
        "LEFT JOIN shipping_method sm ON o.shipping_method_id = sm.shipping_method_id",
        "LEFT JOIN order_detail od ON od.order_id = o.id",
        "WHERE 1=1",
        "<if test='startDate != null'>",
        "  AND o.created_at &gt;= #{startDate}",
        "</if>",
        "<if test='endDate != null'>",
        "  AND o.created_at &lt;= #{endDate}",
        "</if>",
        "<if test='orderNumber != null and orderNumber != \"\"'>",
        "  AND o.order_number LIKE CONCAT('%', #{orderNumber}, '%')",
        "</if>",
        "<if test='resultStatus != null and resultStatus != \"\"'>",
        "  AND o.result_status = #{resultStatus}",
        "</if>",
        "GROUP BY o.id",
        "ORDER BY o.created_at DESC",
        "LIMIT #{offset}, #{safeSize}",
        "</script>"
    })
    @Results(id = "orderPageResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "orderNumber", column = "order_number"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "bonusPointsEarned", column = "bonus_points_earned"),
        @Result(property = "bonusPointsUsed", column = "bonus_points_used"),
        @Result(property = "resultStatus", column = "result_status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "shippingMethodId", column = "shippingMethodId"),
        @Result(property = "shippingMethodName", column = "shippingMethodName"),
        @Result(property = "orderCount", column = "orderCount"),
    })
    List<OrderRes> queryOrders(OrderQueryReq req);

    @Select({
        "<script>",
        "SELECT COUNT(DISTINCT o.id)",
        "FROM `order` o",
        "WHERE 1=1",
        "<if test='startDate != null'>",
        "  AND o.created_at &gt;= #{startDate}",
        "</if>",
        "<if test='endDate != null'>",
        "  AND o.created_at &lt;= #{endDate}",
        "</if>",
        "<if test='orderNumber != null and orderNumber != \"\"'>",
        "  AND o.order_number LIKE CONCAT('%', #{orderNumber}, '%')",
        "</if>",
        "<if test='resultStatus != null and resultStatus != \"\"'>",
        "  AND o.result_status = #{resultStatus}",
        "</if>",
        "</script>"
    })
    long countOrders(OrderQueryReq req);

}
