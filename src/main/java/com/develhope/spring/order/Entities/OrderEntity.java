package com.develhope.spring.order.Entities;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.order.Entities.enums.OrderStatus;
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
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    @Column(nullable = false)
    private LocalDate orderDate;

}

