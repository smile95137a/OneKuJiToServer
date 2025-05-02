package com.one.frontend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRes {

	private Long id;
	private String orderNumber;
	private BigDecimal totalAmount;
	private BigDecimal shippingCost;
	private boolean isFreeShipping;
	private Integer bonusPointsEarned;
	private Integer bonusPointsUsed;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime paidAt;
	private String resultStatus;
	private String paymentMethod;
	private String shippingMethod;
	private String shippingName;
	private String shippingZipCode;
	private String shippingCity;
	private String shippingArea;
	private String shippingAddress;
	private String billingZipCode;
	private String billingName;
	private String billingCity;
	private String billingArea;
	private String billingAddress;
	private String invoice;
	private String trackingNumber;

	private List<OrderDetailRes> orderDetails;
	private Long userId;
	private String vehicle;
	private String billNumber;
	private String state;
	private String donationCode;
	private String type;
	private String uncode;


}
