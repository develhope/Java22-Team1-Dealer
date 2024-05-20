package com.develhope.spring.dealershipstatistics.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RentalDetail {
    private Long rentalId;
    private BigDecimal rentalAmount;
}
