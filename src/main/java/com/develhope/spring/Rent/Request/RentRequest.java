package com.develhope.spring.Rent.Request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RentRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private Double dailyCost;
    private boolean paid;
    private Long vehicleId;
}
