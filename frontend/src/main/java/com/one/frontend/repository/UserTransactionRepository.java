package com.one.frontend.repository;

import com.one.frontend.dto.CreditDto;
import com.one.frontend.model.UserTransaction;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserTransactionRepository {

    // 插入新的交易记录
    @Insert("INSERT INTO user_transaction (user_id, transaction_type, amount, transaction_date, created_at , pay_method) " +
            "VALUES (#{userId}, #{transactionType}, #{amount}, #{localDate}, #{localDate} , #{payMethod})")
    void insertTransaction(@Param("userId") Long userId,
                           @Param("transactionType") String transactionType,
                           @Param("amount") BigDecimal amount,
                           @Param("localDate") LocalDateTime localDate,
    @Param("payMethod") String payMethod);

    @Insert("INSERT INTO user_transaction (user_id, transaction_type, amount, transaction_date, created_at , order_number , status , type) " +
            "VALUES (#{userId}, #{transactionType}, #{amount}, #{localDate}, #{localDate} , #{orderNumber} , 'NO_PAY' , 'MASTER')")
    void insertTransaction2(@Param("userId") Long userId,
                           @Param("transactionType") String transactionType,
                           @Param("amount") BigDecimal amount,
                           @Param("orderNumber") String orderNumber,
                            @Param("localDate") LocalDateTime localDate);

    @Insert("INSERT INTO user_transaction (user_id, transaction_type, amount, transaction_date, created_at , order_number , status , type) " +
            "VALUES (#{userId}, #{transactionType}, #{amount}, #{localDate}, #{localDate} , #{orderNumber} , 'NO_PAY' , 'AFTEE')")
    void insertTransaction3(@Param("userId") Long userId,
                            @Param("transactionType") String transactionType,
                            @Param("amount") BigDecimal amount,
                            @Param("orderNumber") String orderNumber,
                            @Param("localDate") LocalDateTime localDate);


    // 获取某个用户在指定时间段内的交易金额（消费或储值）
    @Select("SELECT COALESCE(SUM(amount), 0) FROM user_transaction " +
            "WHERE user_id = #{userId} AND transaction_type = #{transactionType} " +
            "AND transaction_date BETWEEN #{startDate} AND #{endDate} and pay_method = '1'")
    BigDecimal getTotalAmountForUserAndMonth(@Param("userId") Long userId,
                                             @Param("transactionType") String transactionType,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Select("SELECT * FROM user_transaction WHERE user_id = #{userId} " +
            "AND transaction_date BETWEEN #{startDate} AND #{endDate} order by transaction_date desc")
    List<UserTransaction> findTransactionsByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Select("SELECT * FROM user_transaction WHERE user_id = #{userId} order by transaction_date desc")
    List<UserTransaction> findAllTransactionsByUserId(@Param("userId") Long userId);
    @Update("UPDATE user_transaction SET status = 'IS_PAY', order_id = #{creditDto.orderId} WHERE order_number = #{creditDto.orderNumber}")
    void updateStatus(@Param("creditDto") CreditDto creditDto);
    @Select("select status from user_transaction where order_number = #{orderNumber}")
    String findByOrderNumber(String orderNumber);

    @Select("select * from user_transaction where order_number = #{orderNumber}")
    UserTransaction findByOrderNumber2(String orderNumber);

    @Update("UPDATE user_transaction SET status = 'IS_PAY' WHERE order_number = #{orderNumber}")
    void updateByTop(@Param("orderNumber") String orderNumber);
}
