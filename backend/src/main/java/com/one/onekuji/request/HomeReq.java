package com.one.onekuji.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeReq {
    @Size(max = 32)
    private String vendorOrder; // 客戶訂單編號

    @Size(max = 4)
    private String eshopId; // 客戶代號

    @Min(1)
    @Max(3)
    private int thermosphere; // 溫層(代碼) 0001 常溫, 0002 冷藏, 0003 冷凍

    @Min(1)
    @Max(4)
    private int spec; // 規格(代碼) 0001:60cm, 0002:90cm, 0003:120cm, 0004:150cm
    private String orderAmount; // 商品價值
    @Min(1)
    @Max(3)
    private int serviceType; // 服務型態代碼 3-取貨不付款

    @Size(max = 2048)
    private String internetSite; // 接收狀態的網址

    @Min(0)
    @Max(99999)
    private int amount; // 交易金額

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,5}$")
    private String recipientName; // 取貨人姓名

    @Pattern(regexp = "^\\d{10}$")
    private String recipientMobile; // 取貨人手機電話

    @Size(max = 120)
    private String recipientAddress; // 取貨人地址

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,5}$")
    private String senderName; // 寄件人姓名

    @Pattern(regexp = "^\\d{10}$")
    private String senderMobile; // 寄件人手機電話

    @Size(max = 11)
    private String senderZipCode; // 寄件人郵碼

    @Size(max = 120)
    private String senderAddress; // 寄件人地址

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private String shipmentDate; // 出貨日期 (yyyy-MM-dd)

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private String deliveryDate; // 希望配達日期 (yyyy-MM-dd)

    @Size(max = 4)
    private String productTypeId; // 商品類別(代碼)

    @Size(max = 20)
    private String productName; // 商品名稱

    @Size(max = 32)
    private String chkMac; // 檢查碼

    private String deliveryTime;
}
