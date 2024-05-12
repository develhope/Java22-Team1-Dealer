package com.develhope.spring.User.Entities;

import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.order.Entities.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

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

    private List<OrderEntity> orderEntities;

    private List<PurchaseEntity> purchaseEntities;

    private List<RentEntity> rentEntities;

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
                userModel.getPassword(), userModel.getUserType(), userModel.getOrderEntities(), userModel.purchaseEntities, userModel.getRentEntities());
    }

    public static UserDTO modelToDtoWithList(UserModel userModel) {
        return new UserDTO(userModel.getId(), userModel.getName(), userModel.getSurname(), userModel.getPhoneNumber(), userModel.getEmail(),
                userModel.getPassword(), userModel.getUserType(), userModel.getOrderEntities(), userModel.purchaseEntities, userModel.getRentEntities());
    }

    public static UserDTONoLists modelToDtoWithoutList(UserModel userModel) {
        return new UserDTONoLists(userModel.getId(), userModel.getName(), userModel.getSurname(), userModel.getPhoneNumber(), userModel.getEmail()
                , userModel.getUserType());
    }

    public static UserModel entityToModel(UserEntity userEntity) {
        return new UserModel(userEntity.getId(), userEntity.getName(), userEntity.getSurname(), userEntity.getPhoneNumber(), userEntity.getEmail(),
                userEntity.getPassword(), userEntity.getUserType(), userEntity.getOrderEntities(), userEntity.getPurchaseEntities(), userEntity.getRentEntities());
    }

    public static UserModel dtoToModel(UserDTO userDTO) {
        return new UserModel(userDTO.getId(), userDTO.getName(), userDTO.getSurname(), userDTO.getPhoneNumber(), userDTO.getEmail(),
                userDTO.getPassword(), userDTO.getUserType(), userDTO.getOrderEntities(), userDTO.getPurchaseEntities(), userDTO.getRentEntities());
    }

    public static UserModel dtoToModel(UserDTONoLists userDTO) {
        return new UserModel(userDTO.getId(), userDTO.getName(), userDTO.getSurname(), userDTO.getPhoneNumber(), userDTO.getEmail(),
                 userDTO.getUserType());
    }
}
