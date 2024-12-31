package com.one.service;

import com.one.repository.OrderDetailRepository;
import com.one.request.StoreOrderDetailReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public void save(StoreOrderDetailReq orderDetail) {
        orderDetailRepository.save(orderDetail);
    }
}
