package com.develhope.spring.dealershipStatistics.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetail {
    private Long orderId;
    private BigDecimal orderAmount;
}
