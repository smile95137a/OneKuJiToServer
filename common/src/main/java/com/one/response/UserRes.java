package com.one.response;

import com.one.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRes {
	private Long id;

	private String userUid;

	private String username;

	private String password;
	private String natId;

	private String nickname;

	private String email;

	private String phoneNumber;

	private String city;

	private String area;

	private String address;

	private String addressName;

	private String lineId;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private Long roleId;

	private String status;

	private BigDecimal balance;

	private BigDecimal bonus;

	private BigDecimal sliverCoin;

	private String provider;

	private Set<Role> roles;

	private Long drawCount;
	
	private String invoiceInfo;
	private String invoiceInfoEmail;
	private String zipCode;
	private String vehicle;
}
