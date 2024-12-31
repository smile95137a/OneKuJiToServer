package com.one.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInRes {

    private Long id;

    private String number;

    private BigDecimal sliverPrice;

    private Double probability;
}
