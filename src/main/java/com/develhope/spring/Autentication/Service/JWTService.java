package com.develhope.spring.Autentication.Service;

import com.develhope.spring.Autentication.Entities.DTO.RefreshToken;
import com.develhope.spring.User.Entities.User;
import org.springframework.security.core.userdetails.UserDetails;

public class JWTService {
    private String jwtSignInKey =
    String getUserName(String token) {
    }

    public String generateToken(UserDetails userDetails) {
    }

    public RefreshToken generateRefreshToken(User user) {
    }

    public boolean isRefreshTokenExpired(RefreshToken token) {
    }

    final boolean isTokenAvailable(String token, UserDetails userDetails) {
    }
}
