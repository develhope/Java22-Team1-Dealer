package com.develhope.spring.order.OrderRequest;

import lombok.Data;

@Data
public class OrderRequest {
    private Integer deposit;

    private Boolean paid;

    private String status;

    private Boolean isSold;

    private Long vehicleId;
}
