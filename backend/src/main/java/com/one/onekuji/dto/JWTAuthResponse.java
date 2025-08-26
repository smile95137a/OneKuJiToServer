package com.one.onekuji.dto;

import com.one.onekuji.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JWTAuthResponse {
    private String accessToken;

    private User user;


}
