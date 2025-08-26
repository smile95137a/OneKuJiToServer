package com.one.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DrawDto {
   private Long productId;
   private  List<String> prizeNumbers;

   private String exchangeType;

   private String code;
}
