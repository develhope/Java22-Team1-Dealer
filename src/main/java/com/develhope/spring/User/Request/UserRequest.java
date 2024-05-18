package com.develhope.spring.User.Request;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import lombok.Data;

@Data
public class UserRequest {

    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private UserTypes userType;
}
