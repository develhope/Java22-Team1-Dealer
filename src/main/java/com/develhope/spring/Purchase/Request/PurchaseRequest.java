package com.develhope.spring.Purchase.Request;

import lombok.Data;

@Data
public class PurchaseRequest {

    private Double deposit;

    private Boolean isPaid;

    private String status;

    private Long vehicleId;
}
