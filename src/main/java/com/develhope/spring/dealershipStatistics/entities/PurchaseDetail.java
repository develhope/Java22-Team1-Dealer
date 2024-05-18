package com.develhope.spring.dealershipStatistics.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseDetail {
    private Long purchaseId;
    private BigDecimal purchaseAmount;
}
