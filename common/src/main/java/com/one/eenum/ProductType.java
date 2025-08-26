package com.one.eenum;

public enum ProductType {
    PRIZE("一番賞"),
    GACHA("扭蛋"),
    BLIND_BOX("盲盒"),
    CUSTMER_PRIZE("自製一番賞");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ProductType fromDescription(String description) {
        for (ProductType type : ProductType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with description " + description);
    }

    public static ProductType fromCode(String code) {
        switch (code) {
            case "1":
                return PRIZE;
            case "2":
                return GACHA;
            case "3":
                return BLIND_BOX;
            case "4":
                return CUSTMER_PRIZE;
            default:
                throw new IllegalArgumentException("Invalid code for ProductType: " + code);
        }
    }
}
