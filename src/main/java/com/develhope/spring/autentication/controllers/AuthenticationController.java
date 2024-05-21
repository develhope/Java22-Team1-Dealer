package com.develhope.spring.autentication.controllers;


import com.develhope.spring.autentication.DTO.request.RefreshTokenRequest;
import com.develhope.spring.autentication.DTO.request.SigninRequest;
import com.develhope.spring.autentication.DTO.request.SignupRequest;
import com.develhope.spring.autentication.DTO.response.JWTAuthResponse;
import com.develhope.spring.autentication.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dealer/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<JWTAuthResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<JWTAuthResponse> signin(@RequestBody SigninRequest request) {
        return ResponseEntity.ok(authService.signin(request));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JWTAuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}