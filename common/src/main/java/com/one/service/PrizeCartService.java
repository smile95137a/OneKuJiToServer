package com.one.service;

import com.one.repository.PrizeCartRepository;
import com.one.response.PrizeCartItemRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PrizeCartService {

    @Autowired
    private PrizeCartRepository prizeCartRepository;

        public List<PrizeCartItemRes> getCart(Long userId) {
            return prizeCartRepository.getCart(userId);
        }

    public Long getCartIdByUserId(Long userId) {
        Long cartId = prizeCartRepository.getCartIdByUserId(userId);
        return cartId;
    }
}
