package com.one.frontend.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayCartRes {

    private String paymentMethod; // 支付方式，用户选择的支付方式（例如：信用卡、PayPal）
    private String shippingMethod;

    private String shippingName; // 收货人姓名，订单的收货人姓名
    private String shippingEmail; 
    private String shippingPhone;
    private String shippingZipCode; // 收货地址邮政编码，订单的收货地址邮政编码
    private String shippingCity; // 收货城市，订单的收货地址城市
    private String shippingArea; // 收货区域，订单的收货地址所在区域
    private String shippingAddress; // 收货地址，订单的详细收货地址
    
    private String billingName; // 账单姓名，账单地址的收件人姓名
    private String billingEmail;
    private String billingPhone;
    private String billingZipCode; // 账单地址邮政编码，订单的账单地址邮政编码
    private String billingCity; // 账单城市，订单的账单地址城市
    private String billingArea; // 账单区域，订单的账单地址所在区域
    private String billingAddress; // 账单地址，订单的详细账单地址
    
    private String invoice; // 发票信息，订单的发票相关信息
    
    private List<Long> cartItemIds;

    private List<Long> prizeCartItemIds;


    private String cardNo;

    private String expiryDate;
    private String cvv;
    private String vehicle; //載具
    private String state; //發票捐贈
    private String donationCode; //捐贈馬
    private String cardHolderName;
    private String oPMode;  //通路代號 1:全家 2:萊爾富3: 統一超商 4.OK 超商
    private String express;
    private String shopId;
    private Boolean cardResult;
    private String orderId;
    private String shippingMethodId;
    private String shopName; //門市名稱
    private String shopAddress;
    private String priceType;
}


