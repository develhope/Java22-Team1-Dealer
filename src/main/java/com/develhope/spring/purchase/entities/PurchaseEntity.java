package com.develhope.spring.purchase.entities;

import com.develhope.spring.vehicles.entities.VehicleEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
    @Column(nullable = false)
    private Boolean isPaid;
    @OneToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    @Column(nullable = false)
    private LocalDate purchaseDate;

}
