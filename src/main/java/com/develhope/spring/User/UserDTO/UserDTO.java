package com.develhope.spring.User.UserDTO;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.order.Entities.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private String password;
    private UserTypes userType;

    public UserDTO(String name, String surname, String phoneNumber, String email, String password) {
    }
    //private List<OrderEntity> orderEntities;
}
