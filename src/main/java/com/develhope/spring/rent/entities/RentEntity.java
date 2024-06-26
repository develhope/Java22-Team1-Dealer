package com.develhope.spring.rent.entities;

import com.develhope.spring.vehicles.entities.VehicleEntity;
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
    private VehicleEntity vehicle;

    private BigDecimal dailyCost;
    private BigDecimal totalCost;
    private Boolean isPaid;


    public RentEntity(LocalDate startDate, LocalDate endDate, BigDecimal dailyCost, Boolean isPaid, VehicleEntity vehicle, BigDecimal totalCost, Long id) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.dailyCost = dailyCost;
        this.vehicle = vehicle;
        this.totalCost = totalCost;
        this.id = id;
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