package com.develhope.spring.order.DTO;

import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.User.Entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderDTO {

    private Long orderId;

    private int deposit;

    private boolean paid;

    private String status;

    private boolean isSold;

    private User user;

    private List<PurchaseEntity> purchases;

}
