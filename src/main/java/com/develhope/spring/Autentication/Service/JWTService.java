package com.develhope.spring.Autentication.Service;

import com.develhope.spring.Autentication.Entities.RefreshToken;
import com.develhope.spring.Autentication.Repositories.RefreshTokenRepository;
import com.develhope.spring.User.Entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {
    private String jwtSignInKey = "413F4428472B4B6250655368566D5970337336763979244226452948404D6351";

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String getUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public RefreshToken generateRefreshToken(UserEntity userEntity) {
        Instant refreshTokenExpiredAt = OffsetDateTime.now().plusMonths(1).toInstant();

        List<RefreshToken> tokens = refreshTokenRepository.findByUserEntityInfo(userEntity);
        for (RefreshToken token : tokens) {
            refreshTokenRepository.delete(token);
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .userEntityInfo(userEntity)
                .token(UUID.randomUUID().toString())
                .expDate(refreshTokenExpiredAt)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isRefreshTokenExpired(RefreshToken token) {
        if (token.getExpDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            return true;
        }
        return false;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        OffsetDateTime tokenCreatedAt = OffsetDateTime.now();
        OffsetDateTime tokenExpiredAt = tokenCreatedAt.plusDays(1);

        return Jwts.builder()
                .setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(tokenCreatedAt.toInstant()))
                .setExpiration(Date.from(tokenExpiredAt.toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }


    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSignInKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
