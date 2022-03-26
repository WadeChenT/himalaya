package com.otc.himalaya.controller.dto.auth;

import com.otc.himalaya.bean.ConfigConstant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class JwtAuthenticationRes {
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    private long refreshTime = Instant.now().toEpochMilli() + ConfigConstant.jwtExpirationInMs - ConfigConstant.jwtReFreshInMs;

    public JwtAuthenticationRes(String accessToken, String refreshToken) {
        this.accessToken = tokenType + " " + accessToken;
        this.refreshToken = tokenType + " " + refreshToken;
    }

}
