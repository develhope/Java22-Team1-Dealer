package com.develhope.spring.User.DTO;

import com.develhope.spring.User.entities.Enum.UserTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private UserTypes userType;
}
