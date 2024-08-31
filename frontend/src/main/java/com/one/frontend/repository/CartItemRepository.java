package com.one.frontend.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.one.frontend.model.CartItem;

@Mapper
public interface CartItemRepository {

	@Insert("INSERT INTO cart_item (cart_id, store_product_id, store_product_name, quantity, unit_price, total_price) "
			+ "VALUES (#{cartItem.cartId}, #{cartItem.storeProductId}, #{cartItem.storeProductName}, #{cartItem.quantity}, #{cartItem.unitPrice}, #{cartItem.totalPrice})")
	void addCartItem(@Param("cartItem") CartItem cartItem);

	@Select("SELECT * FROM cart_item WHERE cart_item_id = #{cartItemId}")
	CartItem findById(@Param("cartItemId") Long cartItemId);

	@Select("SELECT * FROM cart_item WHERE cart_id = #{cartId} AND store_product_id = #{storeProductId}")
	CartItem findByCartIdAndStoreProductId(@Param("cartId") Long cartId, @Param("storeProductId") Long storeProductId);

	@Update("UPDATE cart_item SET quantity = #{cartItem.quantity}, total_price = #{cartItem.totalPrice} WHERE cart_item_id = #{cartItem.cartItemId}")
	void updateCartItem(@Param("cartItem") CartItem cartItem);

	@Delete("DELETE FROM cart_item WHERE cart_id = #{cartId} AND cart_item_id = #{cartItemId} ")
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

}
