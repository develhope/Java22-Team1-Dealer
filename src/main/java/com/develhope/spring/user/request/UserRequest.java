package com.develhope.spring.user.request;

import com.develhope.spring.user.entities.Enum.UserTypes;
import lombok.Data;

@Data
public class UserRequest {

    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private UserTypes userType;
}
