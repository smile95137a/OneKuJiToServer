package com.one.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarqueeWithDetailsRes {

	private Long marqueeId;
	private long userId;
	private String username;
	private LocalDateTime createDate;
	private String grade; // 明細表的等級（例如 A賞、B賞）
	private String name; // 明細表的獎項名稱
}
