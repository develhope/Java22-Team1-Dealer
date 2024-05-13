package com.develhope.spring.order.Entities;

import com.develhope.spring.User.Entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table
@Entity
@AllArgsConstructor
public class OrdersLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity buyer;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    OrderEntity orderEntity;

    @OneToOne
    @JoinColumn(name = "seller_id")
    UserEntity seller;

    public OrdersLink(UserEntity buyer, OrderEntity orderEntity) {
        this.buyer = buyer;
        this.orderEntity = orderEntity;
    }
}
