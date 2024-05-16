package com.develhope.spring.Purchase.Request;

import lombok.Data;

@Data
public class PurchaseRequest {
    private Boolean isPaid;
    private Long vehicleId;
}
