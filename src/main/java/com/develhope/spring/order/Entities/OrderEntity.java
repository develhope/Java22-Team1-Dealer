package com.develhope.spring.order.Entities;

import com.develhope.spring.User.Entities.User;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
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
    private Integer deposit;

    @Column(nullable = false, name = "paid")
    private Boolean isPaid;

    @Column(nullable = false, name = "order_status")
    private String status;

    @Column(nullable = false, name = "ordered_sold")
    private Boolean isSold;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicleEntity;
}

