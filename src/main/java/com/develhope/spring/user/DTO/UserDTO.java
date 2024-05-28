package com.develhope.spring.user.DTO;

import com.develhope.spring.user.entities.Enum.UserTypes;
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
