package com.one.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptRes {
    private boolean success;
    private String code;
    private String msg;
    private String totalFee;
    private String orderCode;
    private String phone;
}
