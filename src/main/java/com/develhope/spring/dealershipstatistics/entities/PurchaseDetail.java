package com.develhope.spring.dealershipstatistics.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseDetail {
    private Long purchaseId;
    private BigDecimal purchaseAmount;
}
