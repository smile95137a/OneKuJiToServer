package com.one.service;

import com.one.frontend.repository.CartRepository;
import com.one.frontend.response.CartItemRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;


    public List<CartItemRes> getCart(Long userId) {
        return cartRepository.getCart(userId);
    }

    public Long getCartIdByUserId(Long userId) {
        Long cartId = cartRepository.getCartIdByUserId(userId);
        return cartId;
    }

}
