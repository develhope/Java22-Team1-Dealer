package com.develhope.spring.Autentication.Entities.DTO.Response;

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
