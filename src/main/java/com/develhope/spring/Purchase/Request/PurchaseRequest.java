package com.develhope.spring.Purchase.Request;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import lombok.Data;

@Data
public class PurchaseRequest {

    private Double deposit;

    private Boolean isPaid;

    private PurchaseStatus status;

    private Long vehicleId;

    private Long userId;
}
