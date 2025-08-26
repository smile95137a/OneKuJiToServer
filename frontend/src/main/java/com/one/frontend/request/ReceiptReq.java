package com.one.frontend.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptReq {
    private String timeStamp;
    private String uncode;
    private String idno;
    private String sign;
    private String customerName;
    private String phone;
    private String orderCode;
    private String datetime;
    private String email;
    private int state;
    private String donationCode;
    private Integer taxType;
    private String companyCode;
    private Integer freeAmount;
    private Integer zeroAmount;
    private Integer sales;
    private Integer amount;
    private String totalFee;
    private String content;
    private List<Item> items;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String name;
        private int money;
        private int number;
        private Integer taxType;
        private String remark;
    }
}
