package com.develhope.spring.rent.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RentRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal dailyCost;
    private boolean paid;
    private Long vehicleId;
    private Long userId;

    public RentRequest(LocalDate startDate, LocalDate endDate, BigDecimal dailyCost, boolean paid, Long vehicleId, Long userId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyCost = dailyCost;
        this.paid = paid;
        this.vehicleId = vehicleId;
        this.userId = userId;
    }
}