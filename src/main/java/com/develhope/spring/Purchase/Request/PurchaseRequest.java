package com.develhope.spring.Purchase.Request;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import lombok.Data;

@Data
public class PurchaseRequest {

    private double deposit;

    private boolean isPaid;

    private PurchaseStatus status;

    private Long vehicleId;
}
