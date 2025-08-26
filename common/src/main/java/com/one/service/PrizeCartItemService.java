package com.one.service;

import com.one.model.PrizeCartItem;
import com.one.repository.PrizeCartItemRepository;
import com.one.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PrizeCartItemService {

    @Autowired
    private PrizeCartItemRepository prizeCartItemRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean removeCartItem(Long userId , Long cartId, Long prizeCartItemId) {
        try {
            PrizeCartItem prizeCartItem = prizeCartItemRepository.findById(prizeCartItemId);
            BigDecimal sliverPrice = prizeCartItem.getSliverPrice();

            prizeCartItemRepository.deleteCartItem(cartId, prizeCartItemId);

            userRepository.updateSliverCoin(userId , sliverPrice);

            // 记录回收日志
            prizeCartItemRepository.logPrizeRecycle(userId, prizeCartItem.getProductDetailId(), sliverPrice, String.valueOf(userId));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove cart item", e);
        }
    }

    public List<PrizeCartItem>  findByCartIdAndCartItemList(Long cartId, List<Long> prizeCartItemIds) {
        return prizeCartItemRepository.findByCartIdAndCartItemList(cartId, prizeCartItemIds);
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean removeCartItems(List<Long> prizeCartItemIds, Long cartId) {
        try {
            prizeCartItemRepository.deleteCartItems(cartId, prizeCartItemIds);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove cart item", e);
        }
    }
}
