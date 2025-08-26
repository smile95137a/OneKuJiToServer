package com.one.frontend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderDetailRes {



	private Long orderDetailId;
	private Long productId;
	private String productName;
	private Integer quantity;
	private String productDetailName; // 對應 "product_detail_name"，需要新增
	private BigDecimal unitPrice;
	private Integer totalPrice;
	private StoreProductRes storeProduct;
	private ProductDetailRes productDetailRes;
	private List<String> imageUrls;


}
