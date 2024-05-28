package com.develhope.spring.dealershipstatistics.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetail {
    private Long orderId;
    private BigDecimal orderAmount;
}
