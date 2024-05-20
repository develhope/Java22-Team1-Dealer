package com.develhope.spring.order.entities;

import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.order.entities.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false, name = "deposit")
    private BigDecimal deposit;

    @Column(nullable = false, name = "paid")
    private Boolean isPaid;

    @Column(nullable = false, name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    @Column(nullable = false)
    private LocalDate orderDate;

}

