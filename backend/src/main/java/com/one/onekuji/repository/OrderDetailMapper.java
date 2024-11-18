package com.one.onekuji.repository;

import com.one.onekuji.model.OrderDetail;
import com.one.onekuji.response.OrderDetailRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    @Select("SELECT * FROM order_detail")
    List<OrderDetail> getAllOrderDetails();

    @Select("SELECT * FROM order_detail WHERE order_id = #{id}")
    OrderDetail getOrderDetailById(Long id);

    void deleteOrderDetail(Long id);

    @Select({
            "<script>",
            "SELECT",
            "    od.*,",
            "    COALESCE(sp.product_name, pd.product_name) AS productName,",
            "    COALESCE(sp.image_urls, pd.image_urls) AS imageUrls,",
            "    sp.store_product_id, sp.description, sp.price, sp.stock_quantity,",
            "    pd.product_detail_id , pd.grade , p.product_name as pName",
            "FROM order_detail od",
            "LEFT JOIN store_product sp ON od.store_product_id = sp.store_product_id",
            "LEFT JOIN product_detail pd ON od.product_detail_id = pd.product_detail_id",
            "LEFT JOIN product p ON p.product_id = pd.product_id",
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
            @Result(property = "productDetailRes.grade" , column = "grade")
    })
    List<OrderDetailRes> findOrderDetailsByOrderId(Long orderId);
}
