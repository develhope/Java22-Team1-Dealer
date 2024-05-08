package com.develhope.spring.order.DTO;

import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.User.Entities.User;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;

    private int deposit;

    private boolean paid;

    private String status;

    private boolean isSold;

    private User user;

    @Nullable
    private PurchaseEntity purchase;

    @Nullable
    private RentEntity rent;

    @Nullable
    private User intermediary;

}
