package com.one.onekuji.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackingNumberRequest {
    private String orderId;
    private String trackingNumber;

    // Getters and Setters
}