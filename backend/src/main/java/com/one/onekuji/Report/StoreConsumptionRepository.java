package com.one.onekuji.Report;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface StoreConsumptionRepository {

    @Select("<script>" +
            "SELECT 'day' AS group_type, DATE_FORMAT(draw_time, '%Y-%m-%d') AS time_group, " +
            "       SUM(CASE WHEN pay_type = 1 THEN amount ELSE 0 END) AS gold_amount, " +
            "       SUM(CASE WHEN pay_type = 2 THEN amount ELSE 0 END) AS silver_amount, " +
            "       SUM(CASE WHEN pay_type = 3 THEN amount ELSE 0 END) AS bonus_amount, " +
            "       SUM(CASE WHEN pay_type = 4 THEN amount ELSE 0 END) AS other_amount " +
            "FROM draw_result " +
            "WHERE draw_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(draw_time, '%Y-%m-%d') " +

            "UNION ALL " +

            "SELECT 'week' AS group_type, CONCAT(YEAR(draw_time), '-', WEEK(draw_time)) AS time_group, " +
            "       SUM(CASE WHEN pay_type = 1 THEN amount ELSE 0 END) AS gold_amount, " +
            "       SUM(CASE WHEN pay_type = 2 THEN amount ELSE 0 END) AS silver_amount, " +
            "       SUM(CASE WHEN pay_type = 3 THEN amount ELSE 0 END) AS bonus_amount, " +
            "       SUM(CASE WHEN pay_type = 4 THEN amount ELSE 0 END) AS other_amount " +
            "FROM draw_result " +
            "WHERE draw_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY YEAR(draw_time), WEEK(draw_time) " +

            "UNION ALL " +

            "SELECT 'month' AS group_type, DATE_FORMAT(draw_time, '%Y-%m') AS time_group, " +
            "       SUM(CASE WHEN pay_type = 1 THEN amount ELSE 0 END) AS gold_amount, " +
            "       SUM(CASE WHEN pay_type = 2 THEN amount ELSE 0 END) AS silver_amount, " +
            "       SUM(CASE WHEN pay_type = 3 THEN amount ELSE 0 END) AS bonus_amount, " +
            "       SUM(CASE WHEN pay_type = 4 THEN amount ELSE 0 END) AS other_amount " +
            "FROM draw_result " +
            "WHERE draw_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(draw_time, '%Y-%m') " +

            "UNION ALL " +

            "SELECT 'year' AS group_type, YEAR(draw_time) AS time_group, " +
            "       SUM(CASE WHEN pay_type = 1 THEN amount ELSE 0 END) AS gold_amount, " +
            "       SUM(CASE WHEN pay_type = 2 THEN amount ELSE 0 END) AS silver_amount, " +
            "       SUM(CASE WHEN pay_type = 3 THEN amount ELSE 0 END) AS bonus_amount, " +
            "       SUM(CASE WHEN pay_type = 4 THEN amount ELSE 0 END) AS other_amount " +
            "FROM draw_result " +
            "WHERE draw_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY YEAR(draw_time) " +
            "ORDER BY group_type, time_group DESC" +
            "</script>")
    List<Map<String, Object>> getDrawAmountsByTimeRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



    @Select("<script>" +
            "SELECT 'day' AS group_type, DATE_FORMAT(o.created_at, '%Y-%m-%d') AS time_group, SUM(od.unit_price) AS total_amount " +
            "FROM `order` o " +
            "LEFT JOIN order_detail od ON o.id = od.order_id " +
            "WHERE od.store_product_id IS NOT NULL " +
            "AND o.created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY time_group " +
            "UNION ALL " +
            "SELECT 'week' AS group_type, CONCAT(YEAR(o.created_at), '-', WEEK(o.created_at)) AS time_group, SUM(od.unit_price) AS total_amount " +
            "FROM `order` o " +
            "LEFT JOIN order_detail od ON o.id = od.order_id " +
            "WHERE od.store_product_id IS NOT NULL " +
            "AND o.created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY time_group " +
            "UNION ALL " +
            "SELECT 'month' AS group_type, DATE_FORMAT(o.created_at, '%Y-%m') AS time_group, SUM(od.unit_price) AS total_amount " +
            "FROM `order` o " +
            "LEFT JOIN order_detail od ON o.id = od.order_id " +
            "WHERE od.store_product_id IS NOT NULL " +
            "AND o.created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY time_group " +
            "UNION ALL " +
            "SELECT 'year' AS group_type, YEAR(o.created_at) AS time_group, SUM(od.unit_price) AS total_amount " +
            "FROM `order` o " +
            "LEFT JOIN order_detail od ON o.id = od.order_id " +
            "WHERE od.store_product_id IS NOT NULL " +
            "AND o.created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY time_group " +
            "ORDER BY group_type, time_group DESC" +
            "</script>")
    List<Map<String, Object>> getTotalConsumptionByTimeGroup(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("groupType") String groupType
    );


    @Select("<script>" +
            "SELECT 'day' AS group_type, " +
            "       DATE_FORMAT(ut.created_at, '%Y-%m-%d') AS time_group, " +
            "       SUM(ut.amount) AS total_amount, " +
            "       u.nickname, " +
            "       u.phone_number " +
            "FROM user_transaction ut " +
            "LEFT JOIN `user` u ON ut.user_id = u.id " +
            "WHERE ut.transaction_type = 'DEPOSIT' " +
            "  AND ut.status != 'NO_PAY' " +
            "  AND ut.created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY time_group, u.nickname, u.phone_number " +
            "UNION ALL " +
            "SELECT 'week' AS group_type, " +
            "       CONCAT(YEAR(ut.created_at), '-', WEEK(ut.created_at)) AS time_group, " +
            "       SUM(ut.amount) AS total_amount, " +
            "       u.nickname, " +
            "       u.phone_number " +
            "FROM user_transaction ut " +
            "LEFT JOIN `user` u ON ut.user_id = u.id " +
            "WHERE ut.transaction_type = 'DEPOSIT' " +
            "  AND ut.status != 'NO_PAY' " +
            "  AND ut.created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY time_group, u.nickname, u.phone_number " +
            "UNION ALL " +
            "SELECT 'month' AS group_type, " +
            "       DATE_FORMAT(ut.created_at, '%Y-%m') AS time_group, " +
            "       SUM(ut.amount) AS total_amount, " +
            "       u.nickname, " +
            "       u.phone_number " +
            "FROM user_transaction ut " +
            "LEFT JOIN `user` u ON ut.user_id = u.id " +
            "WHERE ut.transaction_type = 'DEPOSIT' " +
            "  AND ut.status != 'NO_PAY' " +
            "  AND ut.created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY time_group, u.nickname, u.phone_number " +
            "UNION ALL " +
            "SELECT 'year' AS group_type, " +
            "       YEAR(ut.created_at) AS time_group, " +
            "       SUM(ut.amount) AS total_amount, " +
            "       u.nickname, " +
            "       u.phone_number " +
            "FROM user_transaction ut " +
            "LEFT JOIN `user` u ON ut.user_id = u.id " +
            "WHERE ut.transaction_type = 'DEPOSIT' " +
            "  AND ut.status != 'NO_PAY' " +
            "  AND ut.created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY time_group, u.nickname, u.phone_number " +
            "ORDER BY group_type, time_group DESC " +
            "</script>")
    List<Map<String, Object>> getTotalDepositByTimeGroup(
            @Param("groupType") String groupType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Select({
            "<script>",
            "SELECT",
            "    CASE",
            "        WHEN #{groupType} = 'day' THEN DATE(u.created_at)",
            "        WHEN #{groupType} = 'week' THEN CONCAT(YEAR(u.created_at), '-', WEEK(u.created_at))",
            "        WHEN #{groupType} = 'month' THEN DATE_FORMAT(u.created_at, '%Y-%m')",
            "        WHEN #{groupType} = 'year' THEN YEAR(u.created_at)",
            "        WHEN #{groupType} = 'all' THEN DATE(u.created_at)",
            "        ELSE DATE(u.created_at)",
            "    END AS time_group,",
            "    SUM(u.sliver_coin_delta) AS total_sliver_coin,",
            "    SUM(u.bonus_delta) AS total_bonus",
            "FROM user_update_log u",
            "WHERE u.created_at BETWEEN #{startDate} AND #{endDate}",
            "<choose>",
            "   <when test='groupType == \"all\"'>",
            "       GROUP BY time_group",
            "   </when>",
            "   <otherwise>",
            "       GROUP BY time_group",
            "   </otherwise>",
            "</choose>",
            "</script>"
    })
    List<Map<String, Object>> getUserUpdateLogSummary(
            @Param("groupType") String groupType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );



    @Select({
            "<script>",
            "SELECT",
            "    CASE",
            "        WHEN #{groupType} = 'day' THEN DATE(d.sign_in_date)",
            "        WHEN #{groupType} = 'week' THEN CONCAT(YEAR(d.sign_in_date), '-', WEEK(d.sign_in_date))",
            "        WHEN #{groupType} = 'month' THEN DATE_FORMAT(d.sign_in_date, '%Y-%m')",
            "        WHEN #{groupType} = 'year' THEN YEAR(d.sign_in_date)",
            "        WHEN #{groupType} = 'all' THEN DATE(d.sign_in_date)",
            "        ELSE DATE(d.sign_in_date)",
            "    END AS time_group,",
            "    SUM(d.reward_points) AS total_sliver_coin",
            "FROM onekuji.daily_sign_in_records d",
            "WHERE d.sign_in_date BETWEEN #{startDate} AND #{endDate}",
            "<choose>",
            "   <when test='groupType == \"all\"'>",
            "       GROUP BY time_group",
            "   </when>",
            "   <otherwise>",
            "       GROUP BY time_group",
            "   </otherwise>",
            "</choose>",
            "</script>"
    })
    List<Map<String, Object>> getDailySignInSummary(
            @Param("groupType") String groupType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );





    @Select({
            "<script>",
            "SELECT",
            "    CASE",
            "        WHEN #{groupType} = 'day' THEN DATE(pl.recycle_time)",
            "        WHEN #{groupType} = 'week' THEN CONCAT(YEAR(pl.recycle_time), '-', WEEK(pl.recycle_time))",
            "        WHEN #{groupType} = 'month' THEN DATE_FORMAT(pl.recycle_time, '%Y-%m')",
            "        WHEN #{groupType} = 'year' THEN YEAR(pl.recycle_time)",
            "        WHEN #{groupType} = 'all' THEN DATE(pl.recycle_time)",
            "        ELSE DATE(pl.recycle_time)",
            "    END AS time_group,",
            "    SUM(pl.sliver_coin) AS total_sliver_coin",
            "FROM prize_recycle_log pl",
            "WHERE pl.recycle_time BETWEEN #{startDate} AND #{endDate}",
            "<choose>",
            "   <when test='groupType == \"all\"'>",
            "       GROUP BY time_group",
            "   </when>",
            "   <otherwise>",
            "       GROUP BY time_group",
            "   </otherwise>",
            "</choose>",
            "ORDER BY time_group DESC",
            "</script>"
    })
    List<Map<String, Object>> getTotalSliverCoinByRecycleTime(
            @Param("groupType") String groupType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Select({
            "<script>",
            "SELECT",
            "    CASE",
            "        WHEN #{groupType} = 'day' THEN DATE(pl.recycle_time)",
            "        WHEN #{groupType} = 'week' THEN CONCAT(YEAR(pl.recycle_time), '-', LPAD(WEEK(pl.recycle_time), 2, '0'))",
            "        WHEN #{groupType} = 'month' THEN DATE_FORMAT(pl.recycle_time, '%Y-%m')",
            "        WHEN #{groupType} = 'year' THEN YEAR(pl.recycle_time)",
            "        WHEN #{groupType} = 'all' THEN DATE(pl.recycle_time)",
            "        ELSE DATE(pl.recycle_time)",
            "    END AS time_group,",
            "    IFNULL(pd.product_name, '') AS product_detail_name,",
            "    IFNULL(p.product_name, '') AS p_product_name,",
            "    IFNULL(pd.image_urls, '') AS image_urls,",
            "    IFNULL(pd.grade, '') AS grade,",
            "    pl.sliver_coin AS total_sliver_coin",
            "FROM prize_recycle_log pl",
            "LEFT JOIN product_detail pd ON pl.product_detail_id = pd.product_detail_id",
            "LEFT JOIN product p ON pd.product_id = p.product_id",
            "WHERE IFNULL(pd.product_name, '') != ''",
            "<if test='startDate != null and endDate != null'>",
            "    AND pl.recycle_time BETWEEN #{startDate} AND #{endDate}",
            "</if>",
            "<choose>",
            "   <when test='groupType == \"all\"'>",
            "       GROUP BY time_group, pd.product_name, pd.image_urls, pd.grade, pl.sliver_coin",
            "   </when>",
            "   <otherwise>",
            "       GROUP BY time_group, pd.product_name, pd.image_urls, pd.grade, pl.sliver_coin",
            "   </otherwise>",
            "</choose>",
            "ORDER BY time_group DESC",
            "</script>"
    })
    List<Map<String, Object>> getPrizeRecycleReport(
            @Param("groupType") String groupType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );





    @Select({
            "<script>",
            "SELECT",
            "    CASE",
            "        WHEN #{groupType} = 'day' THEN DATE(dr.draw_time)",
            "        WHEN #{groupType} = 'week' THEN CONCAT(YEAR(dr.draw_time), '-', WEEK(dr.draw_time))",
            "        WHEN #{groupType} = 'month' THEN DATE_FORMAT(dr.draw_time, '%Y-%m')",
            "        WHEN #{groupType} = 'year' THEN YEAR(dr.draw_time)",
            "        WHEN #{groupType} = 'all' THEN DATE(dr.draw_time)",
            "        ELSE DATE(dr.draw_time)",
            "    END AS time_group,",
            "    COALESCE(p.product_name, 'Unknown Product') AS product_name,",
            "    COALESCE(pd.product_name, 'Unknown Product Detail') AS product_detail_name,",
            "    COALESCE(pd.image_urls, 'No Image') AS image_urls,",
            "    COALESCE(u.nickname, 'Anonymous') AS nickname,",
            "    CONCAT(dr.amount, ' (',",
            "        CASE",
            "            WHEN dr.pay_type = 1 THEN '金幣'",
            "            WHEN dr.pay_type = 2 THEN '銀幣'",
            "            WHEN dr.pay_type = 3 THEN '紅利'",
            "            ELSE '未知類型'",
            "        END, ')') AS amount_with_type",
            "FROM draw_result dr",
            "LEFT JOIN product_detail pd ON dr.product_detail_id = pd.product_detail_id",
            "LEFT JOIN product p ON dr.product_id = p.product_id",
            "LEFT JOIN `user` u ON dr.user_id = u.id",
            "WHERE dr.pay_type IS NOT NULL",
            "<if test='startDate != null and endDate != null'>",
            "    AND dr.create_date BETWEEN #{startDate} AND #{endDate}",
            "</if>",
            "<choose>",
            "   <when test='groupType == \"all\"'>",
            "       GROUP BY time_group, p.product_name, pd.product_name, pd.image_urls, u.nickname, dr.amount, dr.pay_type",
            "   </when>",
            "   <otherwise>",
            "       GROUP BY time_group, p.product_name, pd.product_name, pd.image_urls, u.nickname, dr.amount, dr.pay_type",
            "   </otherwise>",
            "</choose>",
            "ORDER BY time_group DESC",
            "</script>"
    })
    List<Map<String, Object>> getDrawResultSummary(
            @Param("groupType") String groupType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );






}
