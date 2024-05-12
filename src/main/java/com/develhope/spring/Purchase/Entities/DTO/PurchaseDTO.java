package com.develhope.spring.Purchase.Entities.DTO;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.User.Entities.UserDTONoLists;
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

    private UserDTONoLists user;

    private VehicleEntity vehicleEntity;
}