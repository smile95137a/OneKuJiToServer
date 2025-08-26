package com.one.onekuji.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vendor_order")
public class VendorOrderEntity {

    @Id
    @Column(name = "vendor_order", length = 255, nullable = false)
    private String vendorOrder;    // 廠商訂單編號

    @Column(name = "order_no", length = 255, nullable = false)
    private String orderNo;        // 訂單編號

    @Column(name = "error_code", length = 255, nullable = false)
    private String errorCode;      // 訊息代碼

    @Column(name = "error_message", length = 255)
    private String errorMessage;   // 訊息內容

    @Column(name = "express")
    private String express;

    @Column(name = "status")
    private String status;


    // Getters and Setters
}
