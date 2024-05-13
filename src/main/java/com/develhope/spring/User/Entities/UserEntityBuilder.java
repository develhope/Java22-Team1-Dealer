package com.develhope.spring.User.Entities;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntityBuilder {
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private String password;
    private UserTypes userType;

    public UserEntityBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserEntityBuilder setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public UserEntityBuilder setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;

    }

    public UserEntityBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserEntityBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserEntityBuilder setUserType(UserTypes userType) {
        this.userType = userType;
        return this;
    }

    public UserEntity build() {
        return new UserEntity(this.name, this.surname, this.phoneNumber, this.email, this.password, this.userType);
    }
}
