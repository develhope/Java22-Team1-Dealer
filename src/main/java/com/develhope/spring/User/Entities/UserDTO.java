package com.develhope.spring.User.Entities;

import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.order.Entities.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
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
}
