package com.develhope.spring.Autentication.Entities.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SignupRequest {
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private String password;
    private String userType;
}
