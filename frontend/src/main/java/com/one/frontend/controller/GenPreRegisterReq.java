package com.one.frontend.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenPreRegisterReq {

	private String orderNo;
	private String type;
	private String returnUrl;
}
