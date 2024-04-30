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