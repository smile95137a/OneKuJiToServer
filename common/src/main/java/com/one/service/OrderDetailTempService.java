package com.one.service;

import com.one.model.OrderDetail;
import com.one.model.OrderDetailTemp;
import com.one.repository.OrderDetailTempMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailTempService {

    private final OrderDetailTempMapper orderDetailTempMapper;

    public OrderDetailTempService(OrderDetailTempMapper orderDetailTempMapper) {
        this.orderDetailTempMapper = orderDetailTempMapper;
    }

    public OrderDetailTemp getOrderDetailById(Long id) {
        return orderDetailTempMapper.getOrderDetailById(id);
    }

    public List<OrderDetailTemp> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailTempMapper.getOrderDetailsByOrderId(orderId);
    }

    public void insertOrderDetail(OrderDetail orderDetail) {
        orderDetailTempMapper.insertOrderDetail(orderDetail);
    }

    public void updateOrderDetail(OrderDetailTemp orderDetail) {
        orderDetailTempMapper.updateOrderDetail(orderDetail);
    }

    public void deleteOrderDetail(Long id) {
        orderDetailTempMapper.deleteOrderDetail(id);
    }
}
