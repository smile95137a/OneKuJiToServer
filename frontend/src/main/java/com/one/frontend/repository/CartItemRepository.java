package com.one.frontend.repository;

import com.one.frontend.model.CartItem;
import com.one.frontend.response.CartItemRes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartItemRepository {

    @Insert("INSERT INTO cart_item (cart_id, store_product_id, quantity, unit_price, total_price, is_selected , size) "
            + "VALUES (#{cartItem.cartId}, #{cartItem.storeProductId}, #{cartItem.quantity}, #{cartItem.unitPrice}, #{cartItem.totalPrice}, #{cartItem.isSelected} , #{cartItem.size})")
    void addCartItem(@Param("cartItem") CartItem cartItem);

    @Select("SELECT * FROM cart_item WHERE cart_item_id = #{cartItemId}")
    CartItem findById(@Param("cartItemId") Long cartItemId);

    @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId} AND store_product_id = #{storeProductId}")
    CartItem findByCartIdAndStoreProductId(@Param("cartId") Long cartId, @Param("storeProductId") Long storeProductId);

    @Update("UPDATE cart_item SET quantity = #{cartItem.quantity}, total_price = #{cartItem.totalPrice}, is_selected = #{cartItem.isSelected} WHERE cart_item_id = #{cartItem.cartItemId}")
    void updateCartItem(@Param("cartItem") CartItem cartItem);

    @Delete("DELETE FROM cart_item WHERE cart_id = #{cartId} AND cart_item_id = #{cartItemId}")
    void deleteCartItem(@Param("cartId") Long cartId, @Param("cartItemId") Long cartItemId);

    @Delete({
        "<script>",
        "DELETE FROM cart_item",
        "WHERE cart_id = #{cartId}",
        "AND cart_item_id IN",
        "<foreach item='item' collection='cartItemIds' open='(' separator=',' close=')'>",
        "#{item}",
        "</foreach>",
        "</script>"
    })
    void deleteCartItems(@Param("cartId") Long cartId, @Param("cartItemIds") List<Long> cartItemIds);

    @Select({
        "<script>",
        "SELECT * FROM cart_item",
        "WHERE cart_id = #{cartId}",
        "AND cart_item_id IN",
        "<foreach item='item' index='index' collection='cartItemIds' open='(' separator=',' close=')'>",
        "#{item}",
        "</foreach>",
        "</script>"
    })
    List<CartItem> findByCartIdAndCartItemList(@Param("cartId") Long cartId, @Param("cartItemIds") List<Long> cartItemIds);

    @Select("SELECT quantity FROM cart a join cart_item b on a.cart_id = b.cart_id where user_id = #{userId} and store_product_id = #{storeProductId}")
    CartItemRes findQua(Long userId, Long storeProductId);

    @Select("SELECT * FROM cart_item WHERE cart_id = #{cartId}")
    List<CartItem> find(Long cartId);
}
