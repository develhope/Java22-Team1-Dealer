package com.develhope.spring.Rent.Request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RentRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal dailyCost;
    private boolean paid;
    private Long vehicleId;
    private Long userId;
}