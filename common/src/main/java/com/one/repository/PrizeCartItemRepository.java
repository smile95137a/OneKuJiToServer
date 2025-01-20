package com.one.repository;

import com.one.model.PrizeCartItem;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface PrizeCartItemRepository {
    @Delete("DELETE FROM prize_cart_item WHERE cart_id = #{cartId} AND prize_cart_item_id = #{prizeCartItemId}")
    public void deleteCartItem(Long cartId, Long prizeCartItemId);

    @Select("select * from prize_cart_item where prize_cart_item_id = #{prizeCartItemId}")
    public PrizeCartItem findById(Long prizeCartItemId);
    @Select({
            "<script>",
            "SELECT * FROM prize_cart_item",
            "WHERE cart_id = #{cartId}",
            "AND prize_cart_item_id IN",
            "<foreach item='item' index='index' collection='prizeCartItemIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<PrizeCartItem> findByCartIdAndCartItemList(@Param("cartId")Long cartId, @Param("prizeCartItemIds")List<Long> prizeCartItemIds);
    @Delete({
            "<script>",
            "DELETE FROM prize_cart_item",
            "WHERE cart_id = #{cartId}",
            "AND prize_cart_item_id IN",
            "<foreach item='item' collection='prizeCartItemIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    void deleteCartItems(@Param("cartId")Long cartId, @Param("prizeCartItemIds")List<Long> prizeCartItemIds);


    @Insert({
            "<script>",
            "INSERT INTO prize_cart_item (cart_id, product_detail_id,sliver_price, is_selected, size , quantity) VALUES ",
            "<foreach collection='cartItemList' item='item' separator=','>",
            "(#{item.cartId}, #{item.productDetailId}, #{item.sliverPrice}, #{item.isSelected}, #{item.size} , #{item.quantity})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param("cartItemList") List<PrizeCartItem> cartItemList);
    @Select(
            "select * from prize_cart_item WHERE cart_id = #{cartId} "
    )
    List<PrizeCartItem> find(Long cartId);

    @Insert("INSERT INTO prize_recycle_log (user_id, product_detail_id, sliver_coin, recycle_time, operator) " +
            "VALUES (#{userId}, #{productDetailId}, #{sliverCoin}, NOW(), #{operator})")
    void logPrizeRecycle(@Param("userId") Long userId,
                         @Param("productDetailId") Long productDetailId,
                         @Param("sliverCoin") BigDecimal sliverCoin,
                         @Param("operator") String operator);

}
