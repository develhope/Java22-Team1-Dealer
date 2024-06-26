package com.develhope.spring.order.entities;

import com.develhope.spring.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrdersLinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity buyer;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @OneToOne
    @JoinColumn(name = "seller_id")
    private UserEntity seller;


    public OrdersLinkEntity(UserEntity buyer, OrderEntity order, UserEntity seller) {
        this.buyer = buyer;
        this.order = order;
        this.seller = seller;
    }
}
