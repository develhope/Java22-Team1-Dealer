package com.develhope.spring.Purchase.Entities;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchases")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;
    @Column(nullable = false, name = "deposito")
    private double deposit;
    @Column(nullable = false, name = "pagato")
    private boolean isPaid;
    @Column(nullable = false, name = "stato_ordine")
    private PurchaseStatus status;

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicleEntity;
}
