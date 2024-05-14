package com.develhope.spring.order.Model;

import com.develhope.spring.User.Entities.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrdersLinkModel {
    private Long id;

    private UserModel buyer;

    private UserModel seller;

    private OrderModel order;

    public OrdersLinkModel(Long id, UserModel buyer, OrderModel order) {
        this.id = id;
        this.buyer = buyer;
        this.order = order;
    }
}
