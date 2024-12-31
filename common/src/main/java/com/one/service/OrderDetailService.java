package com.one.service;

import com.one.frontend.repository.OrderDetailRepository;
import com.one.frontend.request.StoreOrderDetailReq;
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
