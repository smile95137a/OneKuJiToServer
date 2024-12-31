package com.one.eenum;

public enum OrderStatus {
    PREPARING_SHIPMENT("準備發貨"),
    SHIPPED("已發貨"),

    SOLD_OUT("售罄"),

    NO_PAY("未付款"),

    FAILED_PAYMENT("付款失敗");


    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
