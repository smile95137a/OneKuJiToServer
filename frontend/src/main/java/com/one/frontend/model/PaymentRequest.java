package com.one.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    private String sendType;         // 傳送型態
    private String payModeNo;        // 付款模式
    private String customerId;       // 商店代號
    private String orderNo;          // 交易單號
    private String amount;           // 交易金額
    private String transCode;        // 交易類別
    private String buyerName;        // 消費者姓名
    private String buyerTelm;        // 消費者手機
    private String buyerMail;        // 消費者Email
    private String buyerMemo;        // 消費備註
    private String cardNo;           // 信用卡號
    private String expireDate;       // 卡片有效日期
    private String cvv;              // 卡片認證碼
    private String transMode;        // 交易模式
    private String installment;      // 期數
    private String returnUrl;        // 授權結果回傳網址
    private String callbackUrl;      // 背景對帳網址
    private String eReturn;          // 是否使用Json回傳
    private String strCheck;         // 交易驗證密碼
    private String paymentMethod;
    private String cardHolderName;
    private Boolean cardResult;
}
