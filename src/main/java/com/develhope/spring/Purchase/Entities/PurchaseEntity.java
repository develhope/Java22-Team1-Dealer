package com.develhope.spring.Purchase.Entities;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchases")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;
    @Column(nullable = false, name = "deposito")
    private Double deposit;
    @Column(nullable = false, name = "pagato")
    private Boolean isPaid;
    @Column(nullable = false, name = "stato_ordine")
    private PurchaseStatus status;

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicleEntity;

    @ManyToOne
    @JoinColumn(name = "link_id")
    private PurchasesLink purchasesLink;
}
