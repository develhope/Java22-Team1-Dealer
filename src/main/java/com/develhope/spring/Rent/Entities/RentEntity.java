package com.develhope.spring.Rent.Entities;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "rentals")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicleId;

    private BigDecimal dailyCost;
    private BigDecimal totalCost;
    private Boolean isPaid;


    public RentEntity(LocalDate startDate, LocalDate endDate, BigDecimal dailyCost, Boolean isPaid, VehicleEntity vehicleId, BigDecimal totalCost) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.dailyCost = dailyCost;
        this.vehicleId = vehicleId;
        this.totalCost = totalCost;
    }

    public BigDecimal calculateTotalCost() {
        if (dailyCost != null && startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            BigDecimal daysBigDecimal = BigDecimal.valueOf(days);
            return dailyCost.multiply(daysBigDecimal);
        }
        return BigDecimal.ZERO;
    }
}