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
public class OrdersLinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity buyer;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    OrderEntity order;

    @OneToOne
    @JoinColumn(name = "seller_id")
    UserEntity seller;

    public OrdersLinkEntity(UserEntity buyer, OrderEntity order) {
        this.buyer = buyer;
        this.order = order;
    }
}
