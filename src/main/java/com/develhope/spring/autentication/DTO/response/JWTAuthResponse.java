package com.develhope.spring.autentication.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class JWTAuthResponse {
    private String authToken;
    private String refreshToken;
}
