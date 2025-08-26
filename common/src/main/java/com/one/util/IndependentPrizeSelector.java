package com.one.util;

import com.one.response.ProductDetailRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IndependentPrizeSelector {

    private static final Random random = new Random();

    public static ProductDetailRes selectPrize(List<ProductDetailRes> productDetails) {
        while (true) {
            for (ProductDetailRes detail : productDetails) {
                if (random.nextDouble() < detail.getProbability()) {
                    return detail;
                }
            }
            // 如果沒有抽中任何獎品，循環會繼續
        }
    }

    public static List<ProductDetailRes> selectPrizes(List<ProductDetailRes> productDetails, int count) {
        List<ProductDetailRes> selectedPrizes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ProductDetailRes prize = selectPrize(productDetails);
            selectedPrizes.add(prize);
        }
        return selectedPrizes;
    }
}