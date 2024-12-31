package com.one.service;

import com.one.frontend.model.Order;
import com.one.frontend.model.OrderTemp;
import com.one.frontend.repository.OrderTempMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderTempService {

    private final OrderTempMapper orderTempMapper;

    public OrderTempService(OrderTempMapper orderTempMapper) {
        this.orderTempMapper = orderTempMapper;
    }

    public void addOrderTemp(Order orderTemp) {
        orderTempMapper.addOrderTemp(orderTemp);
    }

    public OrderTemp getOrderTempById(Integer id) {
        return orderTempMapper.getOrderTempById(id);
    }

    public List<OrderTemp> getAllOrderTemps() {
        return orderTempMapper.getAllOrderTemps();
    }

    public void updateOrderTemp(OrderTemp orderTemp) {
        orderTempMapper.updateOrderTemp(orderTemp);
    }

    public void deleteOrderTemp(Integer id) {
        orderTempMapper.deleteOrderTemp(id);
    }
}
