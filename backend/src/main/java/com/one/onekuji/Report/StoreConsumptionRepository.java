package com.one.onekuji.Report;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface StoreConsumptionRepository {

    @Select({
            "<script>",
            "SELECT",
            "    CASE WHEN #{groupType} = 'day' THEN 'day' ",
            "         WHEN #{groupType} = 'week' THEN 'week' ",
            "         WHEN #{groupType} = 'month' THEN 'month' ",
            "         WHEN #{groupType} = 'year' THEN 'year' END AS group_type,",
            "    <choose>",
            "        <when test='groupType == \"day\"'>",
            "            DATE_FORMAT(draw_time, '%Y-%m-%d') AS time_group",
            "        </when>",
            "        <when test='groupType == \"week\"'>",
            "            CONCAT(YEAR(draw_time), '-', LPAD(WEEK(draw_time), 2, '0')) AS time_group",
            "        </when>",
            "        <when test='groupType == \"month\"'>",
            "            DATE_FORMAT(draw_time, '%Y-%m') AS time_group",
            "        </when>",
            "        <when test='groupType == \"year\"'>",
            "            YEAR(draw_time) AS time_group",
            "        </when>",
            "    </choose>,",
            "    SUM(CASE WHEN pay_type = 1 THEN amount ELSE 0 END) AS gold_amount,",
            "    SUM(CASE WHEN pay_type = 2 THEN amount ELSE 0 END) AS silver_amount,",
            "    SUM(CASE WHEN pay_type = 3 THEN amount ELSE 0 END) AS bonus_amount,",
            "    SUM(CASE WHEN pay_type = 4 THEN amount ELSE 0 END) AS other_amount",
            "FROM draw_result",
            "WHERE draw_time BETWEEN #{startDate} AND #{endDate}",
            "GROUP BY time_group",
            "ORDER BY time_group DESC",
            "</script>"
            })
    List<Map<String, Object>> getDrawAmountsByGroupType(
            @Param("groupType") String groupType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );





    @Select({
            "<script>",
            "SELECT",
            "    CASE",
            "        WHEN #{groupType} = 'day' THEN 'day'",
            "        WHEN #{groupType} = 'week' THEN 'week'",
            "        WHEN #{groupType} = 'month' THEN 'month'",
            "        WHEN #{groupType} = 'year' THEN 'year'",
            "    END AS group_type,",
            "    <choose>",
            "        <when test='groupType == \"day\"'>",
            "            DATE_FORMAT(o.created_at, '%Y-%m-%d') AS time_group",
            "        </when>",
            "        <when test='groupType == \"week\"'>",
            "            CONCAT(YEAR(o.created_at), '-', WEEK(o.created_at)) AS time_group",
            "        </when>",
            "        <when test='groupType == \"month\"'>",
            "            DATE_FORMAT(o.created_at, '%Y-%m') AS time_group",
            "        </when>",
            "        <when test='groupType == \"year\"'>",
            "            YEAR(o.created_at) AS time_group",
            "        </when>",
            "    </choose>,",
            "    SUM(od.unit_price) AS total_amount",
            "FROM `order` o",
            "LEFT JOIN order_detail od ON o.id = od.order_id",
            "WHERE od.store_product_id IS NOT NULL",
            "AND o.created_at BETWEEN #{startDate} AND #{endDate}",
            "GROUP BY time_group",
            "ORDER BY time_group DESC",
            "</script>"
    })
    List<Map<String, Object>> getTotalConsumptionByTimeGroup(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("groupType") String groupType
    );



    @Select({
            "<script>",
            "SELECT",
            "    CASE",
            "        WHEN #{groupType} = 'day' THEN 'day'",
            "        WHEN #{groupType} = 'week' THEN 'week'",
            "        WHEN #{groupType} = 'month' THEN 'month'",
            "        WHEN #{groupType} = 'year' THEN 'year'",
            "    END AS group_type,",
            "    <choose>",
            "        <when test='groupType == \"day\"'>",
            "            DATE_FORMAT(ut.created_at, '%Y-%m-%d') AS time_group",
            "        </when>",
            "        <when test='groupType == \"week\"'>",
            "            CONCAT(YEAR(ut.created_at), '-', WEEK(ut.created_at)) AS time_group",
            "        </when>",
            "        <when test='groupType == \"month\"'>",
            "            DATE_FORMAT(ut.created_at, '%Y-%m') AS time_group",
            "        </when>",
            "        <when test='groupType == \"year\"'>",
            "            YEAR(ut.created_at) AS time_group",
            "        </when>",
            "    </choose>,",
            "    SUM(ut.amount) AS total_amount,",
            "    u.nickname,",
            "    u.phone_number ,",
            "    u.address_name" ,
            "FROM user_transaction ut",
            "LEFT JOIN `user` u ON ut.user_id = u.id",
            "WHERE ut.transaction_type = 'DEPOSIT'",
            "AND ut.status != 'NO_PAY'",
            "AND ut.created_at BETWEEN #{startDate} AND #{endDate}",
            "GROUP BY time_group, u.nickname, u.phone_number",
            "ORDER BY time_group DESC",
            "</script>"
    })
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
            "    u.user_id,",
            "    IFNULL(us.address_name, 'Anonymous') AS addressName,",
            "    u.sliver_coin_delta AS sliver_coin_delta,",
            "    u.bonus_delta AS bonus_delta",
            "FROM user_update_log u",
            "LEFT JOIN `user` us ON u.user_id = us.id",
            "WHERE u.created_at BETWEEN #{startDate} AND #{endDate}",
            "<choose>",
            "   <when test='groupType == \"all\"'>",
            "       ORDER BY u.created_at DESC",
            "   </when>",
            "   <otherwise>",
            "       ORDER BY u.created_at DESC",
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
            "ORDER BY time_group DESC",  // 按 time_group 降序排列
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
            "        ELSE DATE(pl.recycle_time)",
            "    END AS time_group,",
            "    IFNULL(pd.product_name, '') AS product_detail_name,",
            "    IFNULL(p.product_name, '') AS p_product_name,",
            "    IFNULL(pd.image_urls, '') AS image_urls,",
            "    IFNULL(pd.grade, '') AS grade,",
            "    pl.sliver_coin AS total_sliver_coin,",
            "    u.email AS user_email,",
            "    u.nickname AS user_nickname",  // Add email and nickname from user table
            "FROM prize_recycle_log pl",
            "LEFT JOIN product_detail pd ON pl.product_detail_id = pd.product_detail_id",
            "LEFT JOIN product p ON pd.product_id = p.product_id",
            "LEFT JOIN user u ON pl.user_id = u.id",  // Add JOIN with user table
            "WHERE IFNULL(pd.product_name, '') != ''",
            "<if test='startDate != null and endDate != null'>",
            "    AND pl.recycle_time BETWEEN #{startDate} AND #{endDate}",
            "</if>",
            "ORDER BY pl.recycle_time DESC",
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
            "        WHEN #{groupType} = 'week' THEN CONCAT(YEAR(dr.draw_time), '-', LPAD(WEEK(dr.draw_time), 2, '0'))",
            "        WHEN #{groupType} = 'month' THEN DATE_FORMAT(dr.draw_time, '%Y-%m')",
            "        WHEN #{groupType} = 'year' THEN YEAR(dr.draw_time)",
            "        ELSE DATE(dr.draw_time)",
            "    END AS time_group,",
            "    IFNULL(p.product_name, 'Unknown Product') AS product_name,",
            "    IFNULL(pd.product_name, 'Unknown Product Detail') AS product_detail_name,",
            "    IFNULL(pd.image_urls, 'No Image') AS image_urls,",
            "    IFNULL(u.nickname, 'Anonymous') AS nickname,",
            "    CASE WHEN dr.pay_type = 1 THEN dr.amount ELSE 0 END AS balance,",
            "    CASE WHEN dr.pay_type = 2 THEN dr.amount ELSE 0 END AS silver_coin,",
            "    CASE WHEN dr.pay_type = 3 THEN dr.amount ELSE 0 END AS bonus",
            "FROM draw_result dr",
            "LEFT JOIN product_detail pd ON dr.product_detail_id = pd.product_detail_id",
            "LEFT JOIN product p ON dr.product_id = p.product_id",
            "LEFT JOIN `user` u ON dr.user_id = u.id",
            "WHERE dr.pay_type IS NOT NULL",
            "<if test='startDate != null and endDate != null'>",
            "    AND dr.draw_time BETWEEN #{startDate} AND #{endDate}",
            "</if>",
            "ORDER BY dr.draw_time DESC",
            "</script>"
    })
    List<Map<String, Object>> getDrawResultSummary(
            @Param("groupType") String groupType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );









}
