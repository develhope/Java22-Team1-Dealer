package com.develhope.spring.order.OrderRequest;

import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.User.Entities.User;
import lombok.Data;

@Data
public class OrderRequest {
    private int deposit;

    private boolean paid;

    private String status;

    private boolean isSold;

    private User user;

    private PurchaseEntity purchase;
}
