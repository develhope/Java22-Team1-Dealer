package com.develhope.spring.authenticationTest;

import com.develhope.spring.autentication.DTO.request.RefreshTokenRequest;
import com.develhope.spring.autentication.DTO.request.SigninRequest;
import com.develhope.spring.autentication.DTO.request.SignupRequest;
import com.develhope.spring.autentication.DTO.response.JWTAuthResponse;
import com.develhope.spring.autentication.entities.RefreshToken;
import com.develhope.spring.autentication.repositories.RefreshTokenRepository;
import com.develhope.spring.autentication.service.AuthService;
import com.develhope.spring.autentication.service.JWTService;
import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


}
