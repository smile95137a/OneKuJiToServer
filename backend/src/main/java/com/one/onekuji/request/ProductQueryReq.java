package com.one.onekuji.request;

import com.one.onekuji.eenum.PrizeCategory;
import com.one.onekuji.eenum.ProductStatus;
import com.one.onekuji.eenum.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQueryReq {

    private String productName;
    private ProductType productType;
    private PrizeCategory prizeCategory;
    private ProductStatus status;
    private int page = 1;
    private int size = 20;

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getSafeSize() {
        return Math.min(size, 100);
    }
}
