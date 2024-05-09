package com.develhope.spring.order.OrderRequest;

import lombok.Data;

@Data
public class OrderRequest {
    private int deposit;

    private boolean paid;

    private String status;

    private boolean isSold;

    private Long vehicleId;
}
