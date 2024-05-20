package com.develhope.spring.purchase.request;

import lombok.Data;

@Data
public class PurchaseRequest {
    private Boolean isPaid;
    private Long vehicleId;
}
