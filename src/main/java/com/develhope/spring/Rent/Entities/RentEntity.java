package com.develhope.spring.Rent.Entities;

import com.develhope.spring.User.Entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "rentals")
@Data
@AllArgsConstructor
public class RentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "vehicle_id") // Modifica qui
    private Long vehicle; // Modifica qui

    private Double dailyCost;
    private Double totalCost;
    private Boolean isPaid;

    public RentEntity(LocalDate startDate, LocalDate endDate, Double dailyCost, Boolean isPaid, Long vehicleEntity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.dailyCost = dailyCost;
        this.vehicle = vehicleEntity;
    }

    public Double calculateTotalCost() {
        if (dailyCost != null && startDate != null && endDate != null) {
            long days = endDate.toEpochDay() - startDate.toEpochDay();
            return days * dailyCost;
        }
        return null;
    }
}