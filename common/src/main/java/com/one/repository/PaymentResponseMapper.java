package com.one.repository;

import com.one.response.PaymentResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PaymentResponseMapper {

    @Select("SELECT * FROM user_transaction WHERE order_number = #{orderId}")
    PaymentResponse findById(@Param("orderId") String orderId);

    @Select("SELECT * FROM payment_response")
    List<PaymentResponse> findAll();

    @Insert("INSERT INTO payment_response(order_id, send_type, result, ret_msg, currency, amount, date, time, order_no, number, outlay, check_string, bank_name, av_code, invoice_no, e_pay_account , user_id) " +
            "VALUES (#{orderId}, #{sendType}, #{result}, #{retMsg}, #{currency}, #{amount}, #{date}, #{time}, #{orderNo}, #{number}, #{outlay}, #{checkString}, #{bankName}, #{avCode}, #{invoiceNo}, #{ePayAccount} , #{userId})")
    void insert(PaymentResponse paymentResponse);

    @Update("UPDATE payment_response SET send_type = #{sendType}, result = #{result}, ret_msg = #{retMsg}, currency = #{currency}, amount = #{amount}, date = #{date}, time = #{time}, order_no = #{orderNo}, number = #{number}, outlay = #{outlay}, check_string = #{checkString}, bank_name = #{bankName}, av_code = #{avCode}, invoice_no = #{invoiceNo}, e_pay_account = #{ePayAccount} WHERE order_id = #{orderId}")
    void update(PaymentResponse paymentResponse);

    @Delete("DELETE FROM payment_response WHERE order_id = #{orderId}")
    void delete(@Param("orderId") String orderId);
}
