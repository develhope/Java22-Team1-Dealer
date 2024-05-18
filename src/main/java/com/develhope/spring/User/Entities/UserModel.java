package com.develhope.spring.User.Entities;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserModel {
    private Long id;

    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private String password;
    private UserTypes userType;

    public UserModel(Long id, String name, String surname, String phoneNumber, String email, UserTypes userType) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.userType = userType;
    }



    public static UserEntity modelToEntity(UserModel userModel) {
        return new UserEntity(userModel.getId(), userModel.getName(), userModel.getSurname(), userModel.getPhoneNumber(), userModel.getEmail(),
                userModel.getPassword(), userModel.getUserType());
    }

    public static UserDTO modelToDto(UserModel userModel) {
        return new UserDTO(userModel.getId(), userModel.getName(), userModel.getSurname(), userModel.getPhoneNumber(), userModel.getEmail(),
                 userModel.getUserType());
    }


    public static UserModel entityToModel(UserEntity userEntity) {
        return new UserModel(userEntity.getId(), userEntity.getName(), userEntity.getSurname(), userEntity.getPhoneNumber(), userEntity.getEmail(),
                userEntity.getPassword(), userEntity.getUserType());
    }

    public static UserModel dtoToModel(UserDTO userDTO) {
        return new UserModel(userDTO.getId(), userDTO.getName(), userDTO.getSurname(), userDTO.getPhoneNumber(), userDTO.getEmail(),
                 userDTO.getUserType());
    }

}
