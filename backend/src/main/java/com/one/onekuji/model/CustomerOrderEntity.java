package com.one.onekuji.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer_order")
public class CustomerOrderEntity {

    @Id
    @Column(name = "vendor_order", length = 20, nullable = false)
    private String vendorOrder;    // 客戶訂單編號

    @Column(name = "order_no", length = 13, nullable = false)
    private String orderNo;        // 訂單編號

    @Column(name = "error_code", length = 3, nullable = false)
    private String errorCode;      // 訊息代碼

    @Column(name = "error_message", length = 100)
    private String errorMessage;   // 訊息內容

    // Getters and Setters
}
