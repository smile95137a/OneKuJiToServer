package com.one.frontend.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.one.frontend.dto.OrderDetailDto;
import com.one.frontend.model.OrderDetail;
import com.one.frontend.request.StoreOrderDetailReq;
import com.one.frontend.response.OrderDetailRes;

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
    
    @Insert("INSERT INTO order_detail (order_id, store_product_id, quantity, unit_price, result_status, total_price) " +
            "VALUES (#{orderDetail.orderId}, #{orderDetail.storeProductId}, #{orderDetail.quantity}, " +
            "#{orderDetail.unitPrice}, #{orderDetail.resultStatus}, #{orderDetail.totalPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "orderDetail.id")
    void saveOrderDetail(@Param("orderDetail") OrderDetail orderDetail);
    
    
    
    @Select("SELECT od.*, sp.* " +
            "FROM order_detail od " +
            "LEFT JOIN store_product sp ON od.store_product_id = sp.store_product_id " +
            "WHERE od.order_id = #{orderId}")
    @Results({
        @Result(property = "orderDetailId", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "productDetailName", column = "product_detail_name"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "unitPrice", column = "unit_price"),
        @Result(property = "totalPrice", column = "total_price"),
        @Result(property = "storeProduct.storeProductId", column = "store_product_id"),
        @Result(property = "storeProduct.productName", column = "product_name"),
        @Result(property = "storeProduct.description", column = "description"),
        @Result(property = "storeProduct.price", column = "price"),
        @Result(property = "storeProduct.stockQuantity", column = "stock_quantity"),
        @Result(property = "storeProduct.imageUrls", column = "image_urls"),
    })
    List<OrderDetailRes> findOrderDetailsByOrderId(Long orderId);


}
