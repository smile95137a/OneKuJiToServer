package com.one.onekuji.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsRequest {

    @Size(max = 32)
    private String vendorOrder; // 客戶訂單編號

    @Size(max = 3)
    private String mode; // 物流方式

    @Size(max = 4)
    private String eshopId; // 客戶代號

    @Size(max = 6)
    private String storeId; // 門市代號

    private String amount; // 交易金額

    @Size(max = 1)
    private String serviceType; // 服務型態代碼

    private String orderAmount; // 商品價值

    @Size(max = 10)
    private String senderName; // 寄件人姓名

    @Size(max = 10)
    private String sendMobilePhone; // 寄件人手機電話

    @Size(max = 10)
    private String receiverName; // 取貨人姓名

    @Size(max = 10)
    private String receiverMobilePhone; // 取貨人手機電話

    @Size(max = 1)
    private String opMode; // 通路代號

    @Size(max = 2048)
    private String internetSite; // 接收狀態的網址

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private String shipDate; // 出貨日期

    @Size(max = 4)
    private String route; // 理貨路線

    @Size(max = 3)
    private String step; // 理貨路順

    @Size(max = 1)
    private String productType; // 產品類型，酒類請帶1

    @Size(max = 32)
    private String chkMac; // 檢查碼


    @Override
    public String toString() {
        return "LogisticsRequest{" +
                "vendorOrder='" + vendorOrder + '\'' +
                ", mode='" + mode + '\'' +
                ", eshopId='" + eshopId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", amount=" + amount +
                ", serviceType='" + serviceType + '\'' +
                ", orderAmount=" + orderAmount +
                ", senderName='" + senderName + '\'' +
                ", sendMobilePhone='" + sendMobilePhone + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverMobilePhone='" + receiverMobilePhone + '\'' +
                ", opMode='" + opMode + '\'' +
                ", internetSite='" + internetSite + '\'' +
                ", shipDate='" + shipDate + '\'' +
                ", route='" + route + '\'' +
                ", step='" + step + '\'' +
                ", productType='" + productType + '\'' +
                ", chkMac='" + chkMac + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogisticsRequest that = (LogisticsRequest) o;
        return amount == that.amount &&
                orderAmount == that.orderAmount &&
                Objects.equals(vendorOrder, that.vendorOrder) &&
                Objects.equals(mode, that.mode) &&
                Objects.equals(eshopId, that.eshopId) &&
                Objects.equals(storeId, that.storeId) &&
                Objects.equals(serviceType, that.serviceType) &&
                Objects.equals(senderName, that.senderName) &&
                Objects.equals(sendMobilePhone, that.sendMobilePhone) &&
                Objects.equals(receiverName, that.receiverName) &&
                Objects.equals(receiverMobilePhone, that.receiverMobilePhone) &&
                Objects.equals(opMode, that.opMode) &&
                Objects.equals(internetSite, that.internetSite) &&
                Objects.equals(shipDate, that.shipDate) &&
                Objects.equals(route, that.route) &&
                Objects.equals(step, that.step) &&
                Objects.equals(productType, that.productType) &&
                Objects.equals(chkMac, that.chkMac);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendorOrder, mode, eshopId, storeId, amount, serviceType, orderAmount, senderName, sendMobilePhone, receiverName, receiverMobilePhone, opMode, internetSite, shipDate, route, step, productType, chkMac);
    }
}
