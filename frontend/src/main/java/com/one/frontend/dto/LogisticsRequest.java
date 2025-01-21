package com.one.frontend.dto;

import lombok.Data;

@Data
public class LogisticsRequest {
    private String merchantID;
    private String merchantTradeNo;
    private String logisticsSubType;
    private String cvsStoreID;
    private String cvsStoreName;
    private String cvsAddress;
    private String cvsTelephone;
    private String cvsOutside;
    private String extraData;
}
