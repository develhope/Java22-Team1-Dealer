package com.develhope.spring.Rent.Entities;

import com.develhope.spring.Rent.Entities.DTO.RentModel;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "rentals")
@Data
public class RentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id") // Aggiunto per la relazione con l'utente
    private User user; // Aggiunto per la relazione con l'utente

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Long vehicleId;

    private Double dailyCost;
    private Double totalCost;
    private Boolean isPaid;

    public Double calculateTotalCost() {
        if (dailyCost != null && startDate != null && endDate != null) {
            long days = endDate.toEpochDay() - startDate.toEpochDay();
            return days * dailyCost;
        }
        return null;
    }


}