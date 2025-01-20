package com.one.eenum;

public enum StoreProductStatus {
    AVAILABLE("上架"),
    UNAVAILABLE("下架");

    private final String description;

    StoreProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
