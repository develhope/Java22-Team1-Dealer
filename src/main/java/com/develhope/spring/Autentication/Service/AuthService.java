package com.develhope.spring.Autentication.Service;

import com.develhope.spring.Autentication.Entities.DTO.RefreshToken;
import com.develhope.spring.Autentication.Entities.DTO.Request.RefreshTokenRequest;
import com.develhope.spring.Autentication.Entities.DTO.Request.SigninRequest;
import com.develhope.spring.Autentication.Entities.DTO.Request.SignupRequest;
import com.develhope.spring.Autentication.Entities.DTO.Response.JWTAuthResponse;
import com.develhope.spring.Autentication.Repositories.RefreshTokenRepository;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final JWTService jwtService;
    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;


    public JWTAuthResponse signup(SignupRequest request) {
        User user = User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .userType(UserTypes.convertFromString(request.getUserType())).build();

        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        RefreshToken refreshToken = jwtService.generateRefreshToken(user);
        return JWTAuthResponse.builder().authToken(jwt).refreshToken(refreshToken.getToken()).build();
    }


    public JWTAuthResponse signin(SigninRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        String jwt = jwtService.generateToken(user);
        RefreshToken refreshToken = jwtService.generateRefreshToken(user);
        return JWTAuthResponse.builder().authToken(jwt).refreshToken(refreshToken.getToken()).build();
    }


    public JWTAuthResponse refreshToken(RefreshTokenRequest request) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken());

        if (refreshToken.isPresent() && !jwtService.isRefreshTokenExpired(refreshToken.get())) {
            User user = userRepository.findByEmail(refreshToken.get().getUserInfo().getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

            String jwt = jwtService.generateToken(user);

            return JWTAuthResponse.builder().authToken(jwt).refreshToken(refreshToken.get().getToken()).build();
        } else {
            // Handle errors
            return JWTAuthResponse.builder().build();
        }
    }
}
