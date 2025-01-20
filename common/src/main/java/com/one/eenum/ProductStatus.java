package com.one.eenum;

public enum ProductStatus {
    AVAILABLE("上架"),       // 奖品上架
    UNAVAILABLE("下架"),     // 奖品下架
    NOT_AVAILABLE_YET("未上架"), // 奖品未上架
    SOLD_OUT("上架已售完");  // 奖品上架后已售完

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
