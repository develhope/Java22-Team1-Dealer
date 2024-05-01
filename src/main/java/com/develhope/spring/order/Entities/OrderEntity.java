package com.develhope.spring.order.Entities;

import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.User.Entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;
    @Column(nullable = false, name = "deposit")
    private int deposit;
    @Column(nullable = false, name = "paid")
    private boolean paid;
    @Column(nullable = false, name = "order_status")
    private String status;
    @Column(nullable = false, name = "ordered/sold")
    private boolean isSold;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL)
    private List<PurchaseEntity> purchases;
}

