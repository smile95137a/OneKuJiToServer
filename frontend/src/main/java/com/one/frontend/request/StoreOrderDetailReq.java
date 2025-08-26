package com.one.frontend.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreOrderDetailReq {

    private Integer id;

    private Integer orderId;

    private Integer storeProductId;

    private String storeProductName;

    private Integer quantity;

    private BigDecimal unitPrice;
    private Integer totalPrice;
}
