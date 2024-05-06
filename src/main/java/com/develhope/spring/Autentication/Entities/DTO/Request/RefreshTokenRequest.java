package com.develhope.spring.Autentication.Entities.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RefreshTokenRequest {
    private String accessToken;
    private String refreshToken;
}
