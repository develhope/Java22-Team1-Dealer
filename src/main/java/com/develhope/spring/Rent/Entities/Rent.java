package com.develhope.spring.Rent.Entities;

import com.develhope.spring.Vehicles.Entities.Vehicle;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "rentals")44
@Data
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id nooleggio

    private LocalDate startDate; // data di inizio noleggio
    private LocalDate endDate; //data di fine noleggio


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle; //veicolo scelto del noleggio

    private Double dailyCost; // da definire //costo giornaliero noleggio
    private Double totalCost; // da definire //costo totale nol

    // flag pagato
    private Boolean isPaid;

    //calcolo del costo del noleggio (da rivedere)
    public Double calculateTotalCost() {
        if (dailyCost != null && startDate != null && endDate != null) {
            long days = endDate.toEpochDay() - startDate.toEpochDay();
            return days * dailyCost;
        }
        return null;
    }
}