package com.one.onekuji.service;

import com.one.onekuji.request.ShippingMethodReq;
import com.one.onekuji.response.ShippingMethodRes;
import com.one.onekuji.repository.ShippingMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShippingMethodService {

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    public List<ShippingMethodRes> getAllShippingMethods() {
        return shippingMethodRepository.findAll();
    }

    public ShippingMethodRes getShippingMethodById(Long id) {
        return shippingMethodRepository.findById(id);
    }

    public void createShippingMethod(ShippingMethodReq shippingMethodReq) {
        shippingMethodReq.setUpdateDate(LocalDateTime.now());
        shippingMethodReq.setCreateDate(LocalDateTime.now());
        shippingMethodReq.setCode(shippingMethodReq.getName());
        shippingMethodRepository.insert(shippingMethodReq);
    }

    public void updateShippingMethod(Long id, ShippingMethodReq req) {
        ShippingMethodRes res = shippingMethodRepository.findById(id);
        res.setShippingMethodId(id);
        res.setName(req.getName());
        res.setMinSize(req.getMinSize());
        res.setMaxSize(req.getMaxSize());
        res.setShippingPrice(req.getShippingPrice());
        res.setUpdateDate(LocalDateTime.now());
        shippingMethodRepository.update(res);
    }

    public void deleteShippingMethod(Long id) {
        shippingMethodRepository.delete(id);
    }
}
