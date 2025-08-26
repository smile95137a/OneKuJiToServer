package com.one.frontend.eenum;


public enum ShippingMethod {
    HOME_DELIVERY("Home Delivery"),   // 宅配
    SEVEN_ELEVEN_CASH_ON_DELIVERY("7-11 Cash on Delivery"),   // 7-11 货到付款
    SEVEN_ELEVEN_DELIVERY("7-11 Delivery");   // 7-11 宅配

    private final String displayName;

    ShippingMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
