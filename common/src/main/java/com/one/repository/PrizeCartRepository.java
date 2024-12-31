package com.one.repository;

import com.one.frontend.model.PrizeCart;
import com.one.frontend.response.PrizeCartItemRes;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PrizeCartRepository {


    @Insert("INSERT INTO prize_cart (user_id, created_at, updated_at, user_uid) " +
            "VALUES (#{userId}, #{createdAt}, #{updatedAt}, #{userUid})")
    @Options(useGeneratedKeys = true, keyProperty = "cartId")
    void addPrizeCart(PrizeCart prizeCart);

    @Select("SELECT b.*, c.* " +
            "FROM prize_cart a " +
            "JOIN prize_cart_item b ON a.cart_id = b.cart_id " +
            "JOIN product_detail c ON b.product_detail_id = c.product_detail_id " +
            "WHERE a.user_id = #{userId}")
    List<PrizeCartItemRes> getCart(Long userId);
    @Select("select cart_id from prize_cart where user_id = #{userId}")
    Long getCartIdByUserId(Long userId);
}
