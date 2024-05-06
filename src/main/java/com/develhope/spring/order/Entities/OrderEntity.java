package com.develhope.spring.order.Entities;

import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.User.Entities.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false, name = "deposit")
    private int deposit;

    @Column(nullable = false, name = "paid")
    private boolean paid;

    @Column(nullable = false, name = "order_status")
    private String status;

    @Column(nullable = false, name = "ordered_sold")
    private boolean isSold;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Nullable
    private PurchaseEntity purchase;

    @Nullable
    private RentEntity rent;
}

