package com.develhope.spring.User.UserDTO;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import lombok.Data;

@Data
public class UserModel {
    private Long id;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private String password;
    private UserTypes userType;

    public UserModel(String name, String surname, String phoneNumber, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }

    public UserModel(Long id, String name, String surname, String phoneNumber, String email, String password, UserTypes userType) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public static UserDTO modelToDto(UserModel userModel) {
        return new UserDTO(userModel.getName(), userModel.getSurname(), userModel.getPhoneNumber(), userModel.getEmail(), userModel.password);
    }

    public static User modelToEntity(UserModel userModel) {
        return new User(userModel.getName(), userModel.getSurname(), userModel.getPhoneNumber(), userModel.getEmail(), userModel.password);
    }

    public static UserModel entityToModel(User userEntity) {
        return new UserModel(userEntity.getName(), userEntity.getSurname(), userEntity.getPhoneNumber(), userEntity.getEmail(), userEntity.getPassword());
    }

    public static UserModel DtoToModel(UserDTO userDTO) {
        return new UserModel(userDTO.getName(), userDTO.getSurname(), userDTO.getPhoneNumber(), userDTO.getEmail(), userDTO.getPassword());
    }
}
