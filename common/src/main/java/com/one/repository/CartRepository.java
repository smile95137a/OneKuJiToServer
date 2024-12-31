package com.one.repository;

import com.one.frontend.model.Cart;
import com.one.frontend.model.CartItem;
import com.one.frontend.response.CartItemRes;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartRepository {
  
	@Select("SELECT b.*, c.* " +
	        "FROM cart a " +
	        "JOIN cart_item b ON a.cart_id = b.cart_id " +
	        "JOIN store_product c ON b.store_product_id = c.store_product_id " +
	        "WHERE a.user_id = #{userId}")
	List<CartItemRes> getCart(Long userId);
    
    @Insert("INSERT INTO `cart` (user_id, created_at) " +
            "VALUES (#{userId}, #{createdAt})")
    void addCart(Cart cart);
    
    @Select("select cart_id from cart where user_id = #{userId}")
    Long getCartIdByUserId(Long userId);
    
    @Select("select b.quantity , b.unitPrice , b.totalPrice , c.imageUrl , c.specialPrice , d.product_name , c.product_name , d.size , c.size from cart a left join cart_item b on a.cart_id = b.cart_id left join store_product c on b.store_product_id = c.store_product_id  where a.user_id = #{userId} and b.is_pay = false")
    List<CartItem> findByUserUidAndIsPayFalse(Long userId);
}
