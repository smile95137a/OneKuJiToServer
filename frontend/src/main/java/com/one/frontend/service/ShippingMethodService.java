package com.one.frontend.service;

import com.one.frontend.repository.ShippingMethodRepository;
import com.one.frontend.response.ShippingMethodRes;
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
