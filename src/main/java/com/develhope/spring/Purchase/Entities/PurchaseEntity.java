package com.develhope.spring.Purchase.Entities;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.order.Entities.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "deposito")
    private double deposit;
    @Column(nullable = false, name = "pagato")
    private boolean isPaid;
    @Column(nullable = false, name = "stato_ordine")
    private PurchaseStatus status;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;



}