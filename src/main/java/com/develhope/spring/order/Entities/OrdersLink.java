package com.develhope.spring.order.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.User;

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
    User buyer;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    OrderEntity orderEntity;

    @OneToOne
    @JoinColumn(name = "user_id")
    User seller;

    public OrdersLink(User buyer, OrderEntity orderEntity) {
        this.buyer = buyer;
        this.orderEntity = orderEntity;
    }
}
