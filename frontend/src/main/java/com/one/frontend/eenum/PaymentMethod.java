package com.one.frontend.eenum;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),   // 信用卡
    CASH_ON_DELIVERY("Cash on Delivery");   // 货到付款

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
