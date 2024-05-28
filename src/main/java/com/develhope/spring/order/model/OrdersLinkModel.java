package com.develhope.spring.order.model;

import com.develhope.spring.user.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrdersLinkModel {
    private Long id;

    private UserModel buyer;

    private UserModel seller;

    private OrderModel order;

}
