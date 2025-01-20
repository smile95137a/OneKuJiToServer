package com.one.onekuji.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private String username;
    private String roleId;

    public LoginResponse(String token, Long id, String username , String roleId) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.roleId = roleId;
    }
}