package com.one.onekuji.util;

import com.one.onekuji.model.PrizeNumber;
import com.one.onekuji.request.DetailReq;

import java.util.ArrayList;
import java.util.List;

public class PrizeGenerator {

    public List<PrizeNumber> generatePrizeNumbersForDetail(DetailReq detailReq) {
        List<PrizeNumber> prizeNumbers = new ArrayList<>();

        for (int i = 1; i <= detailReq.getQuantity(); i++) {
            PrizeNumber prizeNumber = new PrizeNumber();
            prizeNumber.setProductId(detailReq.getProductId());
            prizeNumber.setProductDetailId(detailReq.getProductDetailId());
            prizeNumber.setNumber(String.valueOf(i));
            prizeNumber.setIsDrawn(false);
            prizeNumber.setLevel(detailReq.getGrade());

            prizeNumbers.add(prizeNumber);
        }

        return prizeNumbers;
    }
}
