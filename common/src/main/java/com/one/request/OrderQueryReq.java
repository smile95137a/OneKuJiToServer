package com.one.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class OrderQueryReq {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Taipei")
	private Date startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Taipei")
	private Date endDate;

	private String orderNumber;
	private String resultStatus;
	private int page = 1;
	private int size = 20;

	public int getOffset() {
		return (page - 1) * size;
	}

	public int getSafeSize() {
		return Math.min(size, 100);
	}
}
