package com.one.onekuji.eenum;

public enum PrizeCategory {

    FIGURE("一番賞") ,
    C3("家電一番賞") ,
    BONUS("紅利一番賞"),
    PRIZESELF("自製一番賞"),
    NONE("無");

    private final String description;

    PrizeCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
