package com.develhope.spring.order.DTO;

import com.develhope.spring.User.Entities.UserDTO;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrdersLinkDTO {
    private Long id;

    private UserDTO buyer;

    @Nullable
    private UserDTO seller;

    private OrderDTO order;

    public OrdersLinkDTO(Long id, UserDTO buyer, OrderDTO order) {
        this.id = id;
        this.buyer = buyer;
        this.order = order;
    }
}
