package com.develhope.spring.Purchase.Entities.DTO;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseDTO {
    private Long id;

    private Double deposit;

    private Boolean isPaid;

    private PurchaseStatus status;

    private VehicleEntity vehicleEntity;

    private UserEntity buyer;
}