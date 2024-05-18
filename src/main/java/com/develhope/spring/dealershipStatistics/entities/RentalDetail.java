package com.develhope.spring.dealershipStatistics.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RentalDetail {
    private Long rentalId;
    private BigDecimal rentalAmount;
}
