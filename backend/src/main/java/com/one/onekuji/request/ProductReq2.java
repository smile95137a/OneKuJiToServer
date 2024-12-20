package com.one.onekuji.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReq2 {
	  private String productName;
      private String description;
      private String productType;
      private String prizeCategory;
      private String price;
      private String sliverPrice;
      private String bonusPrice;
      private String status;
      private String specification;
      private String selectedCategoryId;
      private boolean hasBanner;
      private String categorySelect;
}
