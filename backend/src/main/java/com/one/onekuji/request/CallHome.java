package com.one.onekuji.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallHome {
    @Size(max = 4)
    private String eshopId; // 客戶代號

    @Size(max = 10)
    private String contactName; // 聯絡人姓名

    @Pattern(regexp = "^\\d{10}$")
    private String contactMobile; // 聯絡人手機

    @Size(max = 120)
    private String contactAddress; // 聯絡人地址

    @Min(0)
    private int normalQuantity; // 常溫出貨件數

    @Min(0)
    private int coldQuantity; // 冷藏出貨件數

    @Min(0)
    private int freezeQuantity; // 冷凍出貨件數

    @Pattern(regexp = "^[YN]$")
    private String isContact; // 是否需要電聯 Y:是 N:否

    @Pattern(regexp = "^[YN]$")
    private String isTrolley; // 是否需要推車 Y:是 N:否

    @Size(max = 32)
    private String chkMac; // 檢查碼

    private String vendorOrder;
}
