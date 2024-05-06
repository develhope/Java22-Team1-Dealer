package com.develhope.spring.Autentication.Service;

import com.develhope.spring.Autentication.Entities.DTO.RefreshToken;
import com.develhope.spring.User.Entities.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    String getUserName(String token);
    String generateToken(UserDetails userDetails);

    RefreshToken generateRefreshToken(User user);
    boolean isRefreshTokenExpired(RefreshToken token);
    boolean isTokenAvailable(String token, UserDetails userDetails);
}
