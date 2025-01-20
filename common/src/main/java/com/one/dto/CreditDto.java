package com.one.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreditDto {

    private String orderId;

    private String creditResult;

    private String orderNumber;
}
