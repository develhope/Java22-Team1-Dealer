package com.develhope.spring.order.orderRequest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    private BigDecimal deposit;

    private Boolean paid;

    private String status;

    private Long vehicleId;
}
