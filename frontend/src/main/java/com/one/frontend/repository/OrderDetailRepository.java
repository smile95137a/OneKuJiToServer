package com.one.frontend.repository;

import com.one.frontend.dto.OrderDetailDto;
import com.one.frontend.model.OrderDetail;
import com.one.frontend.request.StoreOrderDetailReq;
import com.one.frontend.response.OrderDetailRes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDetailRepository {


    @Insert("INSERT INTO order_detail (order_id, product_id, product_detail_name, quantity, unit_price, result_status) " +
            "VALUES (#{orderId}, #{productId}, #{productDetailName}, #{quantity}, #{unitPrice}, #{resultStatus})")
    void insertOrderDetailOne(OrderDetailDto orderDetail);


    @Insert({
            "<script>",
            "INSERT INTO order_detail (order_id, product_id, product_detail_name, quantity, unit_price) VALUES",
            "<foreach collection='orderDetailList' item='item' index='index' separator=','>",
            "(#{item.orderId}, #{item.productId}, #{item.productDetailName}, #{item.quantity}, #{item.unitPrice})",
            "</foreach>",
            "</script>"
    })
    void insertOrderDetail(@Param("orderDetailList") List<OrderDetailDto> orderDetailList);



    @Insert("INSERT INTO order_detail (order_id, store_product_id, store_product_name, quantity, unit_price, result_status, total_price) " +
            "VALUES (#{orderDetail.orderId}, #{orderDetail.storeProductId}, #{orderDetail.storeProductName}, " +
            "#{orderDetail.quantity}, #{orderDetail.unitPrice}, #{orderDetail.resultStatus}, #{orderDetail.totalPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "orderDetail.id")
    void save(@Param("orderDetail") StoreOrderDetailReq orderDetail);
    
    @Insert("INSERT INTO order_detail (order_id, store_product_id, quantity, unit_price, total_price, result_item_id, bonus_points_earned , bill_number) " +
            "VALUES (#{orderDetail.orderId}, #{orderDetail.storeProductId}, #{orderDetail.quantity}, #{orderDetail.unitPrice}, #{orderDetail.totalPrice}, #{orderDetail.resultItemId}, #{orderDetail.bonusPointsEarned} , #{orderDetail.billNumber})")
    @Options(useGeneratedKeys = true, keyProperty = "orderDetail.id")
    void saveOrderDetail(@Param("orderDetail") OrderDetail orderDetail);

    @Insert("INSERT INTO order_detail (order_id, product_detail_id, quantity, total_price, result_item_id, bonus_points_earned) " +
            "VALUES (#{orderDetail.orderId}, #{orderDetail.productDetailId}, #{orderDetail.quantity}, #{orderDetail.totalPrice}, #{orderDetail.resultItemId}, #{orderDetail.bonusPointsEarned})")
    @Options(useGeneratedKeys = true, keyProperty = "orderDetail.id")
    void savePrizeOrderDetail(@Param("orderDetail") OrderDetail orderDetail);


    @Select({
            "<script>",
            "SELECT",
            "    od.*,",
            "    COALESCE(sp.product_name, pd.product_name) AS productName,",
            "    COALESCE(sp.image_urls, pd.image_urls) AS imageUrls,",
            "    sp.store_product_id, sp.description, sp.price, sp.stock_quantity,",
            "    pd.product_detail_id , pd.sliver_price , pd.size",
            "FROM order_detail od",
            "LEFT JOIN store_product sp ON od.store_product_id = sp.store_product_id",
            "LEFT JOIN product_detail pd ON od.product_detail_id = pd.product_detail_id",
            "WHERE od.order_id = #{orderId}",
            "</script>"
    })
    @Results({
            @Result(property = "orderDetailId", column = "id"),
            @Result(property = "productId", column = "product_id"),
            @Result(property = "productName", column = "productName"),
            @Result(property = "imageUrls", column = "imageUrls"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "unitPrice", column = "unit_price"),
            @Result(property = "totalPrice", column = "total_price"),

            // Store product mapping
            @Result(property = "storeProduct.storeProductId", column = "store_product_id"),
            @Result(property = "storeProduct.productName", column = "productName"),
            @Result(property = "storeProduct.description", column = "description"),
            @Result(property = "storeProduct.price", column = "price"),
            @Result(property = "storeProduct.stockQuantity", column = "stock_quantity"),
            @Result(property = "storeProduct.imageUrls", column = "imageUrls"),

            // Product detail mapping
            @Result(property = "productDetailRes.productDetailId", column = "product_detail_id"),
            @Result(property = "productDetailRes.productName", column = "productName"),
            @Result(property = "productDetailRes.imageUrls", column = "imageUrls"),
            @Result(property = "productDetailRes.sliverPrice", column = "sliver_price"),
            @Result(property = "productDetailRes.size", column = "size")
    })
    List<OrderDetailRes> findOrderDetailsByOrderId(Long orderId);




    @Select("SELECT od.* , sp.product_detail_id , sp.product_name , sp.description , sp.sliver_price , sp.image_urls " +
            "FROM order_detail od " +
            "LEFT JOIN product_detail sp ON od.product_detail_id = sp.product_detail_id " +
            "WHERE od.order_id = #{orderId}")
    @Results({
            @Result(property = "orderDetailId", column = "id"),
            @Result(property = "productId", column = "product_id"),
            @Result(property = "productDetailName", column = "product_detail_name"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "unitPrice", column = "unit_price"),
            @Result(property = "totalPrice", column = "total_price"),
            @Result(property = "ProductDetail.productDetailId", column = "product_detail_id"),
            @Result(property = "ProductDetail.productName", column = "product_name"),
            @Result(property = "ProductDetail.description", column = "description"),
            @Result(property = "ProductDetail.sliverPrice", column = "sliver_price"),
            @Result(property = "ProductDetail.imageUrls", column = "image_urls"),
    })
    List<OrderDetailRes> findPrizeOrderDetailsByOrderId(Long orderId);


    @Insert({
            "<script>",
            "INSERT INTO order_detail (order_id, product_detail_id, quantity, total_price , bill_number) VALUES ",
            "<foreach collection='orderDetails' item='orderDetail' separator=','>",
            "(#{orderDetail.orderId}, #{orderDetail.productDetailId}, #{orderDetail.quantity}, #{orderDetail.totalPrice} , #{orderDetail.billNumber})",
            "</foreach>",
            "</script>"
    })
    void savePrizeOrderDetailBatch(@Param("orderDetails") List<OrderDetail> orderDetails);
}

