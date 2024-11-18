package com.one.onekuji.service;

import com.one.onekuji.model.Order;
import com.one.onekuji.repository.OrderDetailMapper;
import com.one.onekuji.repository.OrderRepository;
import com.one.onekuji.repository.ShippingMethodRepository;
import com.one.onekuji.request.OrderQueryReq;
import com.one.onekuji.response.OrderRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    public void createOrder(Order order) {
        orderMapper.insertOrder(order);
    }

    public Order getOrderById(Long id) {
        return orderMapper.getOrderById(id);
    }

    public List<OrderRes> getAllOrders(OrderQueryReq req) {
        // 初始化查询参数
        Map<String, Object> params = new HashMap<>();
        // 处理开始日期和结束日期
        LocalDateTime startDate = convertToLocalDateTime(req.getStartDate());
        LocalDateTime endDate = convertToLocalDateTime(req.getEndDate());

        if (startDate != null) {
            startDate = startDate.with(LocalTime.MIN);
        }
        if (endDate != null) {
            endDate = endDate.with(LocalTime.MAX);
        }

        params.put("startDate", startDate);
        params.put("endDate", endDate);

        // 查询订单
        List<OrderRes> ordersByDateRange = orderMapper.findOrdersByDateRange(params);

        // 为每个订单填充订单详情
        List<OrderRes> list = ordersByDateRange.stream()
                .map(order -> {
                    System.out.println(order);
                    order.setOrderDetails(orderDetailMapper.findOrderDetailsByOrderId(order.getId()));
                    order.setOrderCount(orderDetailMapper.findOrderDetailsByOrderId(order.getId()).stream().count());
                    if(order.getShippingMethodId() != null){
                        order.setShippingMethodName(shippingMethodRepository.findById(Long.valueOf(order.getShippingMethodId())).getName());
                    }
                    return order;
                })
                .collect(Collectors.toList());

        // 处理订单状态描述
//        List<OrderRes> orderStatusDescriptions = list.stream()
//                .map(order -> {
//                    String statusDescription;
//                    switch (order.getResultStatus()) {
//                        case "PREPARING_SHIPMENT":
//                            statusDescription = "訂單準備中";
//                            break;
//                        case "SHIPPED":
//                            statusDescription = "已發貨";
//                            break;
//                        default:
//                            statusDescription = "未知狀態";
//                            break;
//                    }
//                    // 设置状态描述
//                    order.setResultStatus(statusDescription);
//                    return order;
//                })
//                .collect(Collectors.toList());

        return list;
    }

    public String updateOrder(Long id, String resultStatus) {
        LocalDateTime now = LocalDateTime.now();
        orderMapper.updateOrder(id, resultStatus , now);
        return "成功";
    }

    public void deleteOrder(Long id) {
        orderMapper.deleteOrder(id);
    }

    private LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert == null ? null : LocalDateTime.ofInstant(dateToConvert.toInstant(), ZoneId.systemDefault());
    }
}
