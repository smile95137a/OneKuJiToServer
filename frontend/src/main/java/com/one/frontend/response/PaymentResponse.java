package com.one.frontend.response;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "payment_response")  // 表名，可以根据实际情况调整
public class PaymentResponse {

    @Id
    @SerializedName("OrderID")
    @Column(name = "order_id", nullable = false, unique = true) // 主键
    private String orderId;    // 订单 ID

    @SerializedName("Send_Type")
    @Column(name = "send_type")
    private String sendType;   // 传送型态

    @Column(name = "result")
    private String result;     // 结果

    @SerializedName("ret_msg")
    @Column(name = "ret_msg")
    private String retMsg;     // 返回消息

    @SerializedName("e_Cur")
    @Column(name = "currency")
    private String currency;   // 货币

    @SerializedName("e_money")
    @Column(name = "amount")
    private String amount;     // 金额

    @SerializedName("e_date")
    @Column(name = "date")
    private String date;       // 日期

    @SerializedName("e_time")
    @Column(name = "time")
    private String time;       // 时间

    @SerializedName("e_orderno")
    @Column(name = "order_no")
    private String orderNo;    // 订单号

    @SerializedName("e_no")
    @Column(name = "number")
    private String number;     // 编号

    @SerializedName("e_outlay")
    @Column(name = "outlay")
    private String outlay;     // 支出

    @SerializedName("str_check")
    @Column(name = "check_string")
    private String checkString; // 检查字符串

    @SerializedName("bankname")
    @Column(name = "bank_name")
    private String bankName;   // 银行名称

    @SerializedName("avcode")
    @Column(name = "av_code")
    private String avCode;     // AV 代码

    @SerializedName("Invoice_No")
    @Column(name = "invoice_no")
    private String invoiceNo;  // 发票号

    @SerializedName("e_payaccount")
    @Column(name = "e_pay_account")
    private String ePayAccount; // 支付账户

    @Column(name = "user_id")
    private Long userId;
}
