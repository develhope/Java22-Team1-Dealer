package com.develhope.spring.Autentication.Service;

import com.develhope.spring.Autentication.Entities.DTO.Request.RefreshTokenRequest;
import com.develhope.spring.Autentication.Entities.DTO.Request.SignupRequest;
import com.develhope.spring.Autentication.Entities.DTO.Response.JWTAuthResponse;

public interface AuthService {
    JWTAuthResponse signup(SignupRequest request);
    JWTAuthResponse signin(SignupRequest request);

    JWTAuthResponse refreshToken(RefreshTokenRequest request);
}
