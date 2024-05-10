package com.develhope.spring.Autentication.Controllers;


import com.develhope.spring.Autentication.Entities.DTO.Request.RefreshTokenRequest;
import com.develhope.spring.Autentication.Entities.DTO.Request.SigninRequest;
import com.develhope.spring.Autentication.Entities.DTO.Request.SignupRequest;
import com.develhope.spring.Autentication.Entities.DTO.Response.JWTAuthResponse;
import com.develhope.spring.Autentication.Service.AuthService;
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