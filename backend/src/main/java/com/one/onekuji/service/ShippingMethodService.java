package com.one.onekuji.service;

import com.one.onekuji.repository.ShippingMethodRepository;
import com.one.onekuji.response.ShippingMethodRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ShippingMethodService {

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;


    public List<ShippingMethodRes> getShippingMethod(BigDecimal size) {
        List<ShippingMethodRes> shippingMethod = shippingMethodRepository.findShippingMethodBySize(size);
        if (shippingMethod == null) {
            return null;
        }
        return shippingMethod;
    }
}