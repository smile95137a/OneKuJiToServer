package com.one.model;

import com.one.eenum.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Schema(description = "訂單模型（簡化版）")
@Table(name = "order_temp")
public class OrderTemp {

    @Schema(description = "訂單唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // 订单ID，唯一标识订单的主键

    @Schema(description = "訂單編號", example = "ORD123456")
    @Column(name = "order_number", length = 50)
    private String orderNumber; // 订单编号，订单的唯一标识符

    @Schema(description = "用戶 ID", example = "1")
    @Column(name = "user_id")
    private Long userId; // 用户ID，关联到下单用户的唯一标识符

    @Schema(description = "總金額", example = "199.98")
    @Column(name = "total_amount")
    private BigDecimal totalAmount; // 订单总金额，订单的最终支付金额

    @Column(name = "shipping_cost")
    private BigDecimal shippingCost; // 运费，订单的运费金额

    @Column(name = "is_free_shipping")
    private Boolean isFreeShipping; // 是否免运费，标识订单是否享受免运费

    @Schema(description = "獲得的獎勳點數", example = "10")
    @Column(name = "bonus_points_earned")
    private Integer bonusPointsEarned; // 获得的积分，用户下单后获得的积分

    @Schema(description = "使用的獎勳點數", example = "5")
    @Column(name = "bonus_points_used")
    private Integer bonusPointsUsed; // 使用的积分，用户下单时使用的积分

    @Schema(description = "創建日期", example = "2024-08-22T15:30:00")
    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建日期，订单创建的时间

    @Schema(description = "更新日期", example = "2024-08-22T15:30:00")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新日期，订单最后一次更新的时间

    @Schema(description = "支付日期", example = "2024-08-22T16:00:00")
    @Column(name = "paid_at")
    private LocalDateTime paidAt; // 支付日期，订单支付的时间

    @Column(name = "result_status", length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatus resultStatus; // 订单状态，标识订单当前的处理状态

    @Column(name = "payment_method")
    private String paymentMethod; // 支付方式，用户选择的支付方式（例如：信用卡、货到付款）

    @Column(name = "shipping_method")
    private String shippingMethod; // 运送方式，用户选择的配送方式（例如：宅配、7-11货到付款、7-11宅配）

    @Column(name = "shipping_name")
    private String shippingName; // 收货人姓名，订单的收货人姓名

    @Column(name = "shipping_email")
    private String shippingEmail; // 收货人邮箱

    @Column(name = "shipping_phone")
    private String shippingPhone; // 收货人电话

    @Column(name = "shipping_zip_code")
    private String shippingZipCode; // 收货地址邮政编码，订单的收货地址邮政编码

    @Column(name = "shipping_city")
    private String shippingCity; // 收货城市，订单的收货地址城市

    @Column(name = "shipping_area")
    private String shippingArea; // 收货区域，订单的收货地址所在区域

    @Column(name = "shipping_address")
    private String shippingAddress; // 收货地址，订单的详细收货地址

    @Column(name = "billing_zip_code")
    private String billingZipCode; // 账单地址邮政编码，订单的账单地址邮政编码

    @Column(name = "billing_name")
    private String billingName; // 账单姓名，账单地址的收件人姓名

    @Column(name = "billing_email")
    private String billingEmail; // 账单邮箱

    @Column(name = "billing_phone")
    private String billingPhone; // 账单电话

    @Column(name = "billing_city")
    private String billingCity; // 账单城市，订单的账单地址城市

    @Column(name = "billing_area")
    private String billingArea; // 账单区域，订单的账单地址所在区域

    @Column(name = "billing_address")
    private String billingAddress; // 账单地址，订单的详细账单地址

    @Column(name = "invoice")
    private String invoice; // 发票信息，订单的发票相关信息

    @Column(name = "tracking_number")
    private String trackingNumber; // 物流追踪号，订单的物流追踪编号

    @Column( name = "cart_item_id")
    private String cartItemIds;
    @Column(name = "express")
    private String express;

    @Column(name = "shop_id")
    private String shopId;

    @Column(name = "OPMode")
    private String OPMode;
}
