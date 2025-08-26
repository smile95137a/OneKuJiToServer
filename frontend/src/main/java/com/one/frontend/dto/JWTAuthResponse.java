package com.one.frontend.dto;

import com.one.frontend.model.User;
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
