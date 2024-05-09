package com.develhope.spring.Purchase.Entities.DTO;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseDTO {
    private Long id;

    private double deposit;

    private boolean isPaid;

    private PurchaseStatus status;

    private VehicleEntity vehicleEntity;
}