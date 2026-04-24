package com.one.frontend.request;

import com.one.frontend.eenum.PrizeCategory;
import com.one.frontend.eenum.ProductStatus;
import com.one.frontend.eenum.ProductType;
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
